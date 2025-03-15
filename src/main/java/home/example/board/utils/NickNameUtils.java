package home.example.board.utils;

public class NickNameUtils {

    public static String nickNameTrim(String nickName) {
        if (nickName == null || nickName.isEmpty()) {
            return "";
        }
        // Trim leading and trailing whitespace
        nickName = nickName.trim();
        int dashIndex = nickName.indexOf("-");
        if (dashIndex != -1) {
            nickName = nickName.substring(0, dashIndex).trim();
        }
        return nickName;
    }

}
