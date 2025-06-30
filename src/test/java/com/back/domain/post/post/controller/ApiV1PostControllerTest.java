package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles // "application-test.yml 활성화 하기 위한 코드"
@SpringBootTest // "스프링 부트 테스트를 위한 어노테이션"
@AutoConfigureMockMvc // "Mock를 자동으로 구성"
@Transactional // "테스트 후 트랜잭션을 롤백하여 DB 초기화"
public class ApiV1PostControllerTest {
    @Autowired // "의존성 주입을 통해 필요한 빈을 가져옵니다."
    private MockMvc mvc; // "MockMvc는 HTTP 요청을 테스트하기 위한 객체입니다."
    @Autowired
    private PostService postService;

    // 회원가입 테스트
    @Test
    @DisplayName("글 쓰기")
    void t1() throws Exception {
        ResultActions resultActions = mvc.perform(
                post("/api/v1/posts") // "API 엔드포인트에 POST 요청을 보냅니다.")
                        .contentType(MediaType.APPLICATION_JSON) // "요청의 Content-Type을 JSON으로 설정합니다."
                        .content("""
                                {
                                    "title": "제목",
                                    "content": "내용"
                                }
                                """) // "요청 본문에 JSON 데이터를 포함합니다."

        ).andDo(print()); // "MockMvc를 사용하여 HTTP 요청을 하고, 출력."

        Post post = postService.findLatest().get(); // "가장 최근에 작성된 글을 가져옵니다."

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class)) // "요청이 ApiV1PostController에서 처리되는지 확인합니다."
                .andExpect(handler().methodName("write")) // "요청이 write 메서드에서 처리되는지 확인합니다."
                .andExpect(status().isCreated()) // "응답 상태가 201 Created인지 확인합니다."
                // 응답 Json 검증
                .andExpect(jsonPath("$.resultCode").value("201-1")) // "resultCode가 201-1인지 확인합니다."
                .andExpect(jsonPath("$.msg").value("%d번 글이 작성되었습니다.".formatted(post.getId()))) // "msg가 'n번 글이 작성되었습니다.' 형식인지 확인합니다."
                .andExpect(jsonPath("$.data.post.id").value(post.getId())) // "data.post.id가 작성된 글의 ID와 일치하는지 확인합니다."
                .andExpect(jsonPath("$.data.post.createDate").value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20)))) // "data.post.createDate가 작성된 글의 생성 날짜와 일치하는지 확인합니다."
                .andExpect(jsonPath("$.data.post.modifyDate").value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20)))) // "data.post.modifyDate가 작성된 글의 수정 날짜와 일치하는지 확인합니다."
                .andExpect(jsonPath("$.data.post.title").value("제목")) // "data.post.title가 요청 본문의 제목과 일치하는지 확인합니다."
                .andExpect(jsonPath("$.data.post.content").value("내용")); // "data.post.content가 요청 본문의 내용과 일치하는지 확인합니다."
    }

    @Test
    @DisplayName("글 수정")
    void t2() throws Exception{
        int id = 1; // "수정할 글의 ID"

        ResultActions resultActions = mvc.perform(
                put("/api/v1/posts/" + id) // "API 엔드포인트에 PUT 요청을 보냅니다.")
                        .contentType(MediaType.APPLICATION_JSON) // "요청의 Content-Type을 JSON으로 설정합니다."
                        .content("""
                                {
                                    "title": "수정된 제목",
                                    "content": "수정된 내용"
                                }
                                """)
                )
                .andDo(print()); // "MockMvc를 사용하여 HTTP 요청을 하고, 출력."

        resultActions
                .andExpect(status().isOk()); // "응답 상태가 200 OK인지 확인합니다."
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class)) // "요청이 ApiV1PostController에서 처리되는지 확인합니다.")
                .andExpect(handler().methodName("modify")) // "요청이 modify 메서드에서 처리되는지 확인합니다.")
                .andExpect(jsonPath("$.resultCode").value("200-1")) // "resultCode가 200-1인지 확인합니다.")
                .andExpect(jsonPath("$.msg").value("%d번 글이 수정되었습니다.".formatted(id))); // "msg 형식을 확인합니다."

    }

    @Test
    @DisplayName("글 삭제")
    void t3() throws Exception {
        int id = 1; // "삭제할 글의 ID"

        ResultActions resultActions = mvc.perform( // "API 엔드포인트에 DELETE 요청을 보냅니다.")
                delete("/api/v1/posts/" + id)
        ).andDo(print());

        resultActions.andExpect(status().isOk()); // "응답 상태가 200 OK인지 확인합니다."

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class)) // "요청이 ApiV1PostController에서 처리되는지 확인합니다."
                .andExpect(handler().methodName("delete")) // "요청이 delete 메서드에서 처리되는지 확인합니다."
                .andExpect(jsonPath("$.resultCode").value("200-1")) // "resultCode가 200-1인지 확인합니다."
                .andExpect(jsonPath("$.msg").value("%d번 글이 삭제되었습니다.".formatted(id))); // "msg 형식을 확인합니다."
    }

    @Test
    @DisplayName("글 조회")
    void t4() throws Exception{
        int id = 1; // "수정할 글의 ID"

        ResultActions resultActions = mvc
                .perform(get("/api/v1/posts/" + id)) // "API 엔드포인트에 PUT 요청을 보냅니다.")
                .andDo(print()); // "MockMvc를 사용하여 HTTP 요청을 하고, 출력."

        resultActions
                .andExpect(status().isOk()); // "응답 상태가 200 OK인지 확인합니다."
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class)) // "요청이 ApiV1PostController에서 처리되는지 확인합니다.")
                .andExpect(handler().methodName("getItem")) // "요청이 modify 메서드에서 처리되는지 확인합니다.")
                .andExpect(jsonPath("$.id").isNumber()) // "resultCode가 200-1인지 확인합니다.")
                .andExpect(jsonPath("$.createDate").isString()) // "data.post.createDate가 작성된 글의 생성 날짜와 일치하는지 확인합니다."
                .andExpect(jsonPath("$.modifyDate").isString()) // "data.post.modifyDate가 작성된 글의 수정 날짜와 일치하는지 확인합니다."
                .andExpect(jsonPath("$.title").isString()) // "data.post.title가 요청 본문의 제목과 일치하는지 확인합니다."
                .andExpect(jsonPath("$.content").isString()); // "data.post.content가 요청 본문의 내용과 일치하는지 확인합니다."
    }

    @Test
    @DisplayName("글 다건조회")
    void t5() throws Exception {
        ResultActions resultActions = mvc // "MockMvc를 사용하여 HTTP 요청을 하고, 출력."
                .perform(get("/api/v1/posts")) // "API 엔드포인트에 GET 요청을 보냅니다.")
                .andDo(print());

        List<Post> posts = postService.findAll(); // "모든 포스트를 가져옵니다."

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class)) // "요청이 ApiV1PostController에서 처리되는지 확인합니다."
                .andExpect(handler().methodName("getItems")) // "요청이 getItems 메서드에서 처리되는지 확인합니다."
                .andExpect(status().isOk()); // "응답 상태가 200 OK인지 확인합니다."

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            resultActions // "각 포스트에 대해 JSON 경로를 검증합니다."
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(post.getId()))
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(post.getTitle()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(post.getContent()));
        }
    }
}