package com.wy.java.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.wy.java.dao.VideoAccessTopNDAO;
import com.wy.java.domain.VideoAccessTopN;

/**
 * 最受欢迎的TOPN课程
 *
 * Web ==> Service ==> DAO
 */
public class VideoAccessTopNServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private VideoAccessTopNDAO dao;

	@Override
	public void init() throws ServletException {
		dao = new VideoAccessTopNDAO();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String day = req.getParameter("day");
		List<VideoAccessTopN> results = dao.query(day);
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter writer = resp.getWriter();
		writer.println(JSON.parseArray(JSON.toJSONString(results)));
		writer.flush();
		writer.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}
}