package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.dto.PostCommentDto;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @Controller + @ResponseBody
@RequestMapping("/api/v1/posts/{postId}/comments") // 댓글의 다건 조회
@RequiredArgsConstructor
public class ApiV1PostCommentController {
    private final PostService postService;

    @GetMapping
    @Transactional(readOnly = true)
    public List<PostCommentDto> getItems(
            @PathVariable int postId
    ) {
        Post post = postService.findById(postId).get();

        return post
                .getComments()
                .stream()
                .map(PostCommentDto::new) // PostComment를 PostCommentDto로 변환)
                .toList();
    }

    @GetMapping("/{id}") // 댓글의 단건 조회
    @Transactional(readOnly = true)
    public PostCommentDto getItem(
            @PathVariable int postId,
            @PathVariable int id
    ){
        Post post = postService.findById(postId).get();
        PostComment postComment = post.findCommentById(id).get();

        return new PostCommentDto(postComment);
    }

    @DeleteMapping("/{id}") // 댓글 삭제
    @Transactional
    public RsData<PostCommentDto> delete(
            @PathVariable int postId,
            @PathVariable int id
    ) {
        Post post = postService.findById(postId).get();
        PostComment postComment = post.findCommentById(id).get();

        postService.deleteComment(post, postComment); // 댓글 삭제

        return new RsData<>("200-1","%번 댓글 삭제완료".formatted(id), new PostCommentDto(postComment));
    }

    record PostCommentModifyReqBody(
            @NotBlank
            @Size(min = 2, max = 5000)
            String content
    ) {
    }

    @PutMapping("/{id}") // 댓글 수정
    @Transactional
    public RsData<Void> modify(
            @PathVariable int postId,
            @PathVariable int id,
            @Valid @RequestBody PostCommentModifyReqBody reqBody
    ) {
        Post post = postService.findById(postId).get();
        PostComment postComment = post.findCommentById(id).get();

        postService.modifyComment(postComment, reqBody.content()); // 댓글 수정

        return new RsData<>(
                "200-1",
                "%d번 댓글 수정완료".formatted(post.getId())
        ); // 수정된 댓글 반환은 하지 않음
    }
}