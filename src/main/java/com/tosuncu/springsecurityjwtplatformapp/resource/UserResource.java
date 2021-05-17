package com.tosuncu.springsecurityjwtplatformapp.resource;

import com.tosuncu.springsecurityjwtplatformapp.domain.HttpResponse;
import com.tosuncu.springsecurityjwtplatformapp.domain.User;
import com.tosuncu.springsecurityjwtplatformapp.domain.UserPrincipal;
import com.tosuncu.springsecurityjwtplatformapp.exception.domain.*;
import com.tosuncu.springsecurityjwtplatformapp.service.UserService;
import com.tosuncu.springsecurityjwtplatformapp.utility.JWTTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.tosuncu.springsecurityjwtplatformapp.constant.FileConstant.*;
import static com.tosuncu.springsecurityjwtplatformapp.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

@Slf4j
@RestController
@RequestMapping(path = {"/", "/user"})

public class UserResource extends ExceptionHandling {


    public static final String NEW_PASSWORD_SENT_BY_EMAIL_ADRESS = "New password sent by email. Adress:";
    public static final String USER_DELETED_SUCCESFULLY = "User deleted succesfully.";
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider tokenProvider;

    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);

        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UserNameExistException, EmailExistException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestParam("firstName") String firsName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNotLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UserNameExistException, NotAnImageFileException {
        User newUser = userService.addNewUser(firsName, lastName, username,
                email, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestParam("currentUsername") String currentUserName,
                                       @RequestParam("firstName") String firsName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("role") String role,
                                       @RequestParam("isActive") String isActive,
                                       @RequestParam("isNotLocked") String isNotLocked,
                                       @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UserNameExistException, NotAnImageFileException {
        User updatedUser = userService.updateUser(currentUserName, firsName, lastName, username,
                email, role, Boolean.parseBoolean(isNotLocked), Boolean.parseBoolean(isActive), profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws MessagingException, EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, NEW_PASSWORD_SENT_BY_EMAIL_ADRESS + email);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESFULLY);
    }

    @PostMapping("/updateprofileimage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,
                                                   @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UserNameExistException, NotAnImageFileException {
        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);

            }
        }
        return byteArrayOutputStream.toByteArray();
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }


    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, tokenProvider.generateJwtToken(user));
        log.info(tokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
