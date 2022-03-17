package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static com.example.demo.utils.ValidationRegex.isRegexPwd;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

//    /**
//     * 회원 조회 API
//     * [GET] /users
//     * 회원 번호 및 이메일 검색 조회 API
//     * [GET] /users? Email=
//     * @return BaseResponse<List<GetUserRes>>
//     */
//    //Query String
//    @ResponseBody
//    @GetMapping("") // (GET) 58.122.17.193:9000/users
//    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
//        try{
//            if(Email == null){
//                List<GetUserRes> getUsersRes = userProvider.getUsers();
//                return new BaseResponse<>(getUsersRes);
//            }
//            // Get Users
//            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(Email);
//            return new BaseResponse<>(getUsersRes);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 58.122.17.193:9000/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // Get Users
        try{
            GetUserRes getUserRes = userProvider.getUser(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {

        // 이메일 빈값 체크
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // 이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        // 비밀번호 정규표현 (최소 8자, 최소 하나의 문자, 하나의 숫자 및 하나의 특수 문자)
        if(!isRegexPwd(postUserReq.getPwd())){
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            // TODO: 로그인 값들에 대한 형식적인 validation 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.

            // 이메일, 비번 빈값 체크
            if (postLoginReq.getEmail().equals("")){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if (postLoginReq.getPwd().equals("")){
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }

            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
//
//    /**
//     * 유저정보변경 API
//     * [PATCH] /users/:userIdx
//     * @return BaseResponse<String>
//     */
//    @ResponseBody
//    @PatchMapping("/{userIdx}")
//    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
//        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 유저네임 변경
//            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getUserName());
//            userService.modifyUserName(patchUserReq);
//
//            String result = "";
//        return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * 유저 피드백 조회 - test용
     * [GET] /users/feedback/:userIdx
     * @return BaseResponse<GetUserFeedbackRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/feedback/{userIdx}") // (GET) 58.122.17.193:9000/users/feedback/:userIdx
    public BaseResponse<GetUserFeedbackRes> getUserFeedback(@PathVariable("userIdx") int userIdx) {
        // Get Users
        try{
            GetUserFeedbackRes getUserFeedbackRes = userProvider.getUserFeedback(userIdx);
            return new BaseResponse<>(getUserFeedbackRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 유저 피드백 조회 (아침, 점심, 저녁)
     * [GET] /users/feedback/time/check?type=1
     * @return BaseResponse<GetUserFeedbackDateRes>
     */
    // Query String
    @ResponseBody
    @GetMapping("/feedback/time/check") // (GET) 58.122.17.193:9000/users/feedback/time/check?type=1
    public BaseResponse<GetUserFeedbackDate2Res> getUserFeedbackDateExist(@RequestBody GetUserFeedbackDateReq getUserFeedbackDateReq, @RequestParam int type) {
        try{
            GetUserFeedbackDate2Res getUserFeedbackDate2Res = userProvider.getUserFeedbackDateExist(getUserFeedbackDateReq, type);
            return new BaseResponse<>(getUserFeedbackDate2Res);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 피드백 조회 (아침, 점심, 저녁)
     * [GET] /users/feedback/time?type=1
     * @return BaseResponse<GetUserFeedbackDateRes>
     */
    // Query String
    @ResponseBody
    @GetMapping("/feedback/time") // (GET) 58.122.17.193:9000/users/feedback/time?type=1
    public BaseResponse<GetUserFeedbackDateRes> getUserFeedbackDate(@RequestBody GetUserFeedbackDateReq getUserFeedbackDateReq, @RequestParam int type) {
        try{
            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();

            GetUserFeedbackDateRes getUserFeedbackDateRes = userProvider.getUserFeedbackDate(getUserFeedbackDateReq, type);
            return new BaseResponse<>(getUserFeedbackDateRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 날짜 별 세부 결과 조회
     * [GET] /users/feedback/detail
     * @return BaseResponse<GetUserFeedbackDetailRes>
     */
    // Query String
    @ResponseBody
    @GetMapping("/feedback/detail") // (GET) 58.122.17.193:9000/users/feedback/detail?idx=1
    public BaseResponse<GetUserFeedbackDetailRes> getUserFeedbackDetail(@RequestParam int idx) {
        try{
            GetUserFeedbackDetailRes getUserFeedbackDetailRes = userProvider.getUserFeedbackDetail(idx);
            return new BaseResponse<>(getUserFeedbackDetailRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }


}
