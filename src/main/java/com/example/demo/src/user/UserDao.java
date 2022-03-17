package com.example.demo.src.user;


import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
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
    

    public int createUser(PostUserReq postUserReq, String rawPwd){
        String createUserQuery = "insert into User (nickname, email, pwd, rawPwd)\n" +
                "values (?, ?, ?, ?)";

        Object[] createUserParams = new Object[]{postUserReq.getNickname(), postUserReq.getEmail(), postUserReq.getPwd(), rawPwd};
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

    public GetUserFeedbackRes getUserFeedback(int userIdx) {
        String getUserQuery = "select idx, userIdx, feedbackMsg, mappingVideoUrl from UserFeedback where idx = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserFeedbackRes(
                        rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getString("feedbackMsg"),
                        rs.getString("mappingVideoUrl")
                ),
                getUserParams);
    }


    public GetUserFeedbackDate2Res getUserFeedbackDateExist(GetUserFeedbackDateReq getUserFeedbackDateReq, int type) {
        String getUserQuery = "";
        int getUserParams1 = getUserFeedbackDateReq.getUserIdx();
        int getUserParams2 = getUserFeedbackDateReq.getYear();
        int getUserParams3 = getUserFeedbackDateReq.getMonth();
        int getUserParams4 = getUserFeedbackDateReq.getDay();

        // 아침
        if (type == 1) {
            getUserQuery = "select exists(select idx, score, brushtime\n" +
                    "from UserFeedback\n" +
                    "where userIdx = ? and year(createdAt) = ? and month(createdAt) = ? and day(createdAt) = ? and\n" +
                    "      (hour (createdAt) < 12 and hour (createdAt) > 3)\n" +
                    "order by userIdx desc limit 1) as existOrNot";
        }

        // 점심
        else if (type == 2) {
            getUserQuery = "select exists(select idx, score, brushtime\n" +
                    "from UserFeedback\n" +
                    "where userIdx = ? and year(createdAt) = ? and month(createdAt) = ? and day(createdAt) = ? and\n" +
                    "      (hour (createdAt) > 11 and hour (createdAt) < 16)\n" +
                    "order by createdAt desc limit 1) as existOrNot";
        }

        // 저녁
        else if (type == 3) {
            getUserQuery = "select exists(select idx, score, brushtime\n" +
                    "from UserFeedback\n" +
                    "where userIdx = ? and year(createdAt) = ? and month(createdAt) = ? and day(createdAt) = ?\n" +
                    "  and ((hour (createdAt) < 4)\n" +
                    "   or (hour (createdAt) > 15 and hour (createdAt) < 24))\n" +
                    "order by createdAt desc limit 1) as existOrNot";
        }

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserFeedbackDate2Res(
                        rs.getInt("existOrNot")
                ),
                getUserParams1, getUserParams2, getUserParams3, getUserParams4);
    }

    public GetUserFeedbackDateRes getUserFeedbackDate(GetUserFeedbackDateReq getUserFeedbackDateReq, int type){
        String getUserQuery = "";
        int getUserParams1 = getUserFeedbackDateReq.getUserIdx();
        int getUserParams2 = getUserFeedbackDateReq.getYear();
        int getUserParams3 = getUserFeedbackDateReq.getMonth();
        int getUserParams4 = getUserFeedbackDateReq.getDay();

        // 아침
        if (type == 1) {
            getUserQuery = "select idx, score, brushtime\n" +
                    "from UserFeedback\n" +
                    "where userIdx = ? and year(createdAt) = ? and month(createdAt) = ? and day(createdAt) = ? and\n" +
                    "      (hour (createdAt) < 12 and hour (createdAt) > 3)\n" +
                    "order by userIdx desc limit 1";
        }

        // 점심
        else if (type == 2) {
            getUserQuery = "select idx, score, brushtime\n" +
                    "from UserFeedback\n" +
                    "where userIdx = ? and year(createdAt) = ? and month(createdAt) = ? and day(createdAt) = ? and\n" +
                    "      (hour (createdAt) > 11 and hour (createdAt) < 16)\n" +
                    "order by createdAt desc limit 1";
        }

        // 저녁
        else if (type == 3) {
            getUserQuery = "select idx, score, brushtime\n" +
                    "from UserFeedback\n" +
                    "where userIdx = ? and year(createdAt) = ? and month(createdAt) = ? and day(createdAt) = ?\n" +
                    "  and ((hour (createdAt) < 4)\n" +
                    "   or (hour (createdAt) > 15 and hour (createdAt) < 24))\n" +
                    "order by createdAt desc limit 1";
        }

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserFeedbackDateRes(
                        rs.getInt("idx"),
                        rs.getInt("score"),
                        rs.getInt("brushtime")
                ),
                getUserParams1, getUserParams2, getUserParams3, getUserParams4);
    }

    public GetUserFeedbackDetailRes getUserFeedbackDetail(int idx) {
        String getUserQuery = "select (select date_format(createdAt, '%Y년 %m월 %d일')\n" +
                "from UserFeedback\n" +
                "where idx = ?\n" +
                "order by createdAt desc limit 1) as brushDate, (select date_format(createdAt, '%H시 %i분 %s초')\n" +
                "from UserFeedback\n" +
                "where idx = ?\n" +
                "order by createdAt desc limit 1) as brushTime, score, feedbackMsg\n" +
                "from UserFeedback\n" +
                "where idx = ?";

        int getUserParams = idx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserFeedbackDetailRes(
                        rs.getString("brushDate"),
                        rs.getString("brushTime"),
                        rs.getInt("score"),
                        rs.getString("feedbackMsg")
                ),
                getUserParams, getUserParams, getUserParams);
    }


}
