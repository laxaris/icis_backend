package com.icis.demo.Service;


import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

import com.icis.demo.Entity.*;
import com.icis.demo.System.AuthenticationResponse;
import com.icis.demo.Utils.EncryptionUtil;
import com.icis.demo.Utils.JWTUtil;
import com.icis.demo.Utils.OBSUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.icis.demo.Utils.MailUtil;

@Service
public class AuthorizationService {

    private final UserService userService;
    private final OBSUtil obsUtil;
    private final EncryptionUtil encryptionUtil;
    private final JWTUtil JWTUtil;
    private final MailUtil mailUtil;
    private final Dictionary<String, Integer> emailCodeDictionary = new Hashtable<>();

    @Autowired
    public AuthorizationService(UserService userService, OBSUtil obsUtil, EncryptionUtil encryptionUtil, JWTUtil JWTUtil, MailUtil mailUtil) {
        this.userService = userService;
        this.obsUtil = obsUtil;
        this.encryptionUtil = encryptionUtil;
        this.JWTUtil = JWTUtil;
        this.mailUtil = mailUtil;
    }

    public OnlineUser isSessionValid(String jwt) {
        OnlineUser onlineUser = userService.getOnlineUser(jwt.trim());
        String email = onlineUser.getEmail();
        boolean result = JWTUtil.validateJWTToken(jwt, email);
        if (result) {
            return onlineUser;
        }
        return null;
    }

    public AuthenticationResponse isAuthorizedSignUpStudent(String email, String password) {

        AuthenticationResponse authResponse = new AuthenticationResponse();

        if (userService.existsByEmail(email)) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Registration failed: Email already in use.");
            return authResponse;
        }

        ObsPerson obsPerson = obsUtil.isRealStudent(email);

        if (obsPerson != null && obsPerson.getRole().equals("student")) {
            String encryptedPassword;

            try {
                encryptedPassword = encryptionUtil.encryptPassword(password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            userService.createStudentUser(obsPerson, encryptedPassword);
            authResponse.setSuccess(true);
            authResponse.setMessage("Student Registration successful.");
            return authResponse;
        }
        authResponse.setSuccess(false);
        authResponse.setMessage("Student Registration failed.");
        return authResponse;
    }

    public AuthenticationResponse isAuthorizedSignUpCompany(String name, String email,boolean isForeign, String password) {
        AuthenticationResponse authResponse = new AuthenticationResponse();
        String encryptedPassword;

        if (userService.existsByEmail(email)) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Registration failed: Email already in use.");
            return authResponse;
        }

        try {
            encryptedPassword = encryptionUtil.encryptPassword(password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        userService.createCompanyUser(name, email,isForeign, encryptedPassword);
        authResponse.setSuccess(true);
        authResponse.setMessage("Company Registration successful.");
        return authResponse;
    }

    public AuthenticationResponse isAuthorizedSignUpStaff(String email, String password) {
        AuthenticationResponse authResponse = new AuthenticationResponse();

        if (userService.existsByEmail(email)) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Registration failed: Email already in use.");
            return authResponse;
        }

        ObsPerson obsPerson = obsUtil.isRealStaff(email);

        if (obsPerson != null && !obsPerson.getRole().equals("student")) {
            String encryptedPassword;

            try {
                encryptedPassword = encryptionUtil.encryptPassword(password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            userService.createStaffUser(obsPerson, encryptedPassword);
            authResponse.setSuccess(true);
            authResponse.setMessage("Staff Registration successful.");
            return authResponse;
        }

        authResponse.setSuccess(false);
        authResponse.setMessage("Staff Registration failed.");
        return authResponse;
    }

    public AuthenticationResponse isAuthorizedLoginStudent(String email, String password) {
        AuthenticationResponse authResponse = new AuthenticationResponse();

        try {
            Student student = userService.getStudentUser(email);
            if (student.getName() == null) {
                authResponse.setSuccess(false);
                authResponse.setMessage("No user found with the given email.");
                return authResponse;
            }

            if (!student.getPassword().equals(encryptionUtil.encryptPassword(password))) {
                authResponse.setSuccess(false);
                authResponse.setMessage("Incorrect password.");
                return authResponse;
            }

            boolean isUserEligible = userService.isUserEligible(email);
            if (!isUserEligible) {
                authResponse.setSuccess(false);
                authResponse.setMessage("User not eligible for login.");
                return authResponse;
            }

            String jwtToken = JWTUtil.createJWTToken(email);

            OnlineUser onlineUser = new OnlineUser(jwtToken, student.getName(), email);
            boolean isOnlineUserExist = userService.existByEmailOnlineUser(email);

            if (!isOnlineUserExist) {
                userService.saveOnlineUser(onlineUser);
            }else{
                userService.updateOnlineUser(onlineUser);
            }

            authResponse.setSuccess(true);
            authResponse.setMessage("Student Login successful.");
            authResponse.setOnlineUser(onlineUser);

            return authResponse;

        } catch (Exception e) {
            authResponse.setSuccess(false);
            authResponse.setMessage("An error occurred during login.");
            return authResponse;
        }
    }

    public AuthenticationResponse isAuthorizedLoginCompany(String email, String password) {
        AuthenticationResponse authResponse = new AuthenticationResponse();

        Company company = userService.getCompanyUser(email);
        if (company.getCompanyName() == null) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Email is not registered to the system.");
            return authResponse;
        }

        if (!"approved".equalsIgnoreCase(company.getStatus())) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Company account is not approved.");
            return authResponse;
        }

        try {
            if (!company.getPassword().equals(encryptionUtil.encryptPassword(password))) {
                authResponse.setSuccess(false);
                authResponse.setMessage("Incorrect password.");
                return authResponse;
            }

            String jwtToken = JWTUtil.createJWTToken(email);

            OnlineUser onlineUser = new OnlineUser(jwtToken, company.getCompanyName(), email);
            boolean isOnlineUserExist = userService.existByEmailOnlineUser(email);

            if (!isOnlineUserExist) {
                userService.saveOnlineUser(onlineUser);
            }else{
                userService.updateOnlineUser(onlineUser);
            }

            authResponse.setSuccess(true);
            authResponse.setMessage("Company Login successful.");
            authResponse.setOnlineUser(onlineUser);

            return authResponse;
        } catch (Exception e) {
            authResponse.setSuccess(false);
            authResponse.setMessage("An error occurred during the login process.");
        }

        return authResponse;
    }

    public AuthenticationResponse isAuthorizedLoginStaff(String email, String password) {
        AuthenticationResponse authResponse = new AuthenticationResponse();

        Staff staff = userService.getStaffUser(email);
        if (staff.getName() == null) {
            authResponse.setSuccess(false);
            authResponse.setMessage("No staff member found with the given email.");
            return authResponse;
        }

        try {
            if (!staff.getPassword().equals(encryptionUtil.encryptPassword(password))) {
                authResponse.setSuccess(false);
                authResponse.setMessage("Incorrect password.");
                return authResponse;
            }

            String jwtToken = JWTUtil.createJWTToken(email);

            OnlineUser onlineUser = new OnlineUser(jwtToken, staff.getName(), email);
            boolean isOnlineUserExist = userService.existByEmailOnlineUser(email);

            if (!isOnlineUserExist) {
                userService.saveOnlineUser(onlineUser);
            }else{
                userService.updateOnlineUser(onlineUser);
            }

            authResponse.setSuccess(true);
            authResponse.setMessage("Staff Login successful.");
            authResponse.setOnlineUser(onlineUser);

            return authResponse;
        } catch (Exception e) {
            authResponse.setSuccess(false);
            authResponse.setMessage("An error occurred during the login process.");
        }
        return authResponse;
    }

    public boolean sendResetPasswordEmail(String email) {
        Integer randomCode = createRandomEmailCode();
        if(userService.existsByEmail(email)){
            mailUtil.sendMail(email, "Reset Password ICIS", "Your reset password code is: " + randomCode);
            emailCodeDictionary.put(email, randomCode);
            return true;
        }
        return false;
    }

    private Integer createRandomEmailCode(){
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    public boolean isEmailCodeValid(String email, int code){
        Integer codeDict = emailCodeDictionary.get(email);
        System.out.println(codeDict);
        return codeDict.equals(code);
    }

    public void changePassword(String email, String password) {
        String encryptedPassword;
        try {
            encryptedPassword = encryptionUtil.encryptPassword(password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userService.changePassword(email, encryptedPassword);
    }

    public boolean ifUserExists(String email){
        return userService.existsByEmail(email);
    }
}
