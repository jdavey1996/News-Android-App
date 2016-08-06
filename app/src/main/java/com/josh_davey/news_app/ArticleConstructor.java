package com.josh_davey.news_app;

public class ArticleConstructor {
    public String ArticleNum;
    public String ArticleTitle;
    public String ArticleDesc;

    public ArticleConstructor(String ArticleNum, String Title, String Desc)
    {
        this.ArticleNum = ArticleNum;
        this.ArticleTitle = Title;
        this.ArticleDesc = Desc;
    }

    public String getArticleNum() {
        return ArticleNum;
    }

    public String getArticleDesc() {
        return ArticleDesc;
    }

    public String getArticleTitle() {
        return ArticleTitle;
    }
}
