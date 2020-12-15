package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

@Data
public class CoinArticleModel extends BaseModel {

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *获取的快讯ID
	 */
	
	private Long aid;
	
	/**
	 *标题
	 */
	
	private String url;
	
	/**
	 *标题
	 */
	
	private String title;
	
	/**
	 *摘要
	 */
	
	private String summary;
	
	/**
	 *内容
	 */
	
	private String content;
	
	/**
	 *快讯时间
	 */
	
	private String publishedAt;
	
	/**
	 *标题
	 */
	
	private String resource;
	
	/**
	 *标题
	 */
	
	private String resourceUrl;
	
	/**
	 *标题
	 */
	
	private String author;
	
	/**
	 *标题
	 */
	
	private String thumbnail;
	


}
