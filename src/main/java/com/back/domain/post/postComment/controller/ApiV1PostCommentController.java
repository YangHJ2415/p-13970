package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.dto.PostCommentDto;
import com.back.domain.post.postComment.entity.PostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // @Controller + @ResponseBody
@RequestMapping("/api/v1/posts/{postId}/comments") // 댓글의 다건 조회
@RequiredArgsConstructor
public class ApiV1PostCommentController {
    private final PostService postService;

    @GetMapping
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
    public PostCommentDto getItem(
            @PathVariable int postId,
            @PathVariable int id
    ){
        Post post = postService.findById(postId).get();
        PostComment postComment = post.findCommentById(id).get();

        return new PostCommentDto(postComment);
    }
}