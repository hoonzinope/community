package home.example.board.utils;

public class UserInfoCheckUtils {

    public static boolean isValidate(String user_name, String user_pw, String user_email) {
        boolean flag = true;
        if(!isUsername(user_name)){
            System.out.println("user_name is not valid");
            flag = false;
        }
        if (!isPassword(user_pw)){
            System.out.println("user_pw is not valid");
            flag = false;
        }
        if (!isEmail(user_email)){
            System.out.println("user_email is not valid");
            flag = false;
        }
        return flag;
    }

    private static boolean isUsername(String username) {
        return username.matches("^[a-zA-Z0-9]{4,20}$");
    }
    public static boolean isPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$");
    }
    private static boolean isEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

}
