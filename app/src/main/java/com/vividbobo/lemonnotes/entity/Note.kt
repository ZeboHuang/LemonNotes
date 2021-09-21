package com.vividbobo.lemonnotes.entity


class Note(var text: String, val images: List<String>?, val video: String?, val spans: List<Span>?) {
    /**
     * @param text 源文本
     * @param images 所有图片uri
     * @param video 视频uri     目前只支持一个吧
     * @param spans 文本样式
     * */
}