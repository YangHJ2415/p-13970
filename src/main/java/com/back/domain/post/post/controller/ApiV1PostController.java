package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @Controller + @ResponseBody
@RequestMapping("/api/v1/posts") // @GetMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    @GetMapping
    @Transactional(readOnly = true)
    public List<PostDto> getItems() {
        List<Post> items = postService.findAll();

        return items
                .stream()
                .map(post -> new PostDto(post)) // post를 PostDto로 변환, .map(PostDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PostDto getItem(@PathVariable int id) {
        Post post = postService.findById(id).get();

        return new PostDto(post);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public RsData<PostDto> delete(@PathVariable int id) {
        Post post = postService.findById(id).get();

        postService.delete(post); // 포스트 삭제

        return new RsData<>(
                "200-1",
                "%d번 글이 삭제되었습니다.".formatted(id),
                new PostDto(post)
        ); // 삭제된 포스트 반환
    }

    record PostWriteReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String title,
            @NotBlank
            @Size(min = 2, max = 5000)
            String content
    ) {
    }

    record PostWriteResBody(
            long totalCount,
            PostDto post
    ){
    }

    @PostMapping
    @Transactional
    public RsData<PostWriteResBody> write(@Valid @RequestBody PostWriteReqBody form) {
        Post post = postService.write(form.title, form.content);

        return new RsData<>(
                "200-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostWriteResBody(postService.count(), new PostDto(post))
        ); // 생성된 포스트 반환
    }
}