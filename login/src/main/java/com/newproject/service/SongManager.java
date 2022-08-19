package com.newproject.service;

import com.newproject.entity.Song;
import com.newproject.entity.User;
import com.newproject.mapper.SongMapper;
import com.newproject.mapper.UserMapper;
import com.newproject.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Component
public class SongManager {

    public SongManager() {
    }

    public int addSong(String name, String url, String artist) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        SongMapper songMapper = sqlSession.getMapper(SongMapper.class);
        int res = songMapper.addSong(new Song(url, artist, name));
        sqlSession.commit();
        sqlSession.close();
        return res;
        //提交事务
    }

    public Song getSong(String name){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        SongMapper songMapper = sqlSession.getMapper(SongMapper.class);
        Song res = songMapper.getSong(name);
        sqlSession.commit();
        sqlSession.close();
        return res; // this can possible be null
    }

    public int rateSong(Song song, int rating) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        SongMapper songMapper = sqlSession.getMapper(SongMapper.class);
        int avgRating = (song.getTotalNumRate() * song.getRating() + rating) / song.setTotalNumRate();
        song.setRating(avgRating);
        int res = songMapper.rateSong(song);
        sqlSession.commit();
        sqlSession.close();
        return res;
    }
}
