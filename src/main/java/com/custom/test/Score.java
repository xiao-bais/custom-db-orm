package com.custom.test;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import com.custom.enums.DbMediaType;

/**
 * @Author Xiao-Bai
 * @Date 2021/8/29
 * @Description
 */
@DbTable(table = "score")
public class Score {

    @DbKey
    private int id;

    @DbField(fieldType = DbMediaType.DbInt)
    private int stuId;

    @DbField(fieldType = DbMediaType.DbInt)
    private int score;

    public Score(int id, int stuId, int score) {
        this.id = id;
        this.stuId = stuId;
        this.score = score;
    }

    public Score(int stuId, int score) {
        this.stuId = stuId;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStuId() {
        return stuId;
    }

    public void setStuId(int stuId) {
        this.stuId = stuId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
