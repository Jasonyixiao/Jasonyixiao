package com.newproject.mapper;

import com.newproject.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper
public interface SongMapper {
    Song getSong(String songName);
    int addSong(Song song); // 0 is failure, 1 is success
    int rateSong(Song song); // 0 is failure, 1 is success

}
