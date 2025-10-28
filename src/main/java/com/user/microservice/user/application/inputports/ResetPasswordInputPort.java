// user/application/inputports/*.java
package com.user.microservice.user.application.inputports;



public interface ResetPasswordInputPort {

    void reset(String token, String newPassword);
}
