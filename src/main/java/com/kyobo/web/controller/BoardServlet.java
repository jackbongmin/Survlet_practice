package com.kyobo.web.controller;

import com.kyobo.web.dao.JdbcBoardDao;
import com.kyobo.web.dao.JdbcCommentDao;
import com.kyobo.web.dao.JdbcLikeDao;
import com.kyobo.web.model.BoardPost;
import com.kyobo.web.model.Member;
import com.kyobo.web.service.BoardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 타임라인 피드형 게시판 컨트롤러
 * - @MultipartConfig: 이미지 파일 업로드 지원
 */
@WebServlet("/board")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,    // 1MB 이상 시 디스크 임시 저장
        maxFileSize = 1024 * 1024 * 10,     // 개별 파일 최대 10MB
        maxRequestSize = 1024 * 1024 * 20   // 전체 요청 최대 20MB
)
public class BoardServlet extends HttpServlet{

    private BoardService boardService;

    @Override
    public void init() {
        this.boardService = new BoardService(
                new JdbcBoardDao(),
                new JdbcCommentDao(),
                new JdbcLikeDao()
        );
    }



}
