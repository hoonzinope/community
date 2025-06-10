package home.example.board.service.admin.user;

import org.springframework.stereotype.Service;

@Service
public class UserAdminReadCommentService {
    private final UserCommentAdminDAO userCommentAdminDAO;
    public UserAdminReadCommentService(UserCommentAdminDAO userCommentAdminDAO) {
        this.userCommentAdminDAO = userCommentAdminDAO;
    }
}
