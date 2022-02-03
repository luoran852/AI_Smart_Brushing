package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(){
        String getUsersQuery = "select idx, nickname, email, pwd from User";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("idx"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("pwd"))
                );
    }

    public List<GetUserRes> getUsersByEmail(String email){
        String getUsersByEmailQuery = "select idx, nickname, email, pwd from User where email =?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("idx"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("pwd")),
                getUsersByEmailParams);
    }

    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select idx, nickname, email, pwd from User where idx = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("idx"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("pwd")),
                getUserParams);
    }
    

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (nickname, email, pwd)\n" +
                "values (?, ?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getNickname(), postUserReq.getEmail(), postUserReq.getPwd()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }
//
//    public int modifyUserName(PatchUserReq patchUserReq){
//        String modifyUserNameQuery = "update UserInfo set userName = ? where userIdx = ? ";
//        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};
//
//        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
//    }
//
    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select idx, nickname, email, pwd from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("idx"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("pwd")
                ),
                getPwdParams
                );

    }

    public GetUserFeedbackRes getUserFeedback(int userIdx){
        String getUserQuery = "select idx, userIdx, feedbackMsg, mappingVideoUrl, date from UserFeedback where idx = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserFeedbackRes(
                        rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getString("feedbackMsg"),
                        rs.getString("mappingVideoUrl"),
                        rs.getString("date")),
                getUserParams);
    }



}
