package home.example.board.repository.admin;

import home.example.board.DTO.adminApiDTO.userComment.AdminUserCommentPagingDTO;
import home.example.board.DTO.adminApiDTO.userComment.UserCommentReadRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserAdminCommentMapper {
    // Define methods for fetching comments by user sequence
    List<AdminUserCommentPagingDTO> getCommentByUserSeq(UserCommentReadRequestDTO userCommentReadRequestDTO);

    // Define method for counting comments by user sequence
    int getCommentCountByUserSeq(UserCommentReadRequestDTO userCommentReadRequestDTO);
}
