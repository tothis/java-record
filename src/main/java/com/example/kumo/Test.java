package com.example.kumo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundaryBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static java.awt.Font.BOLD;

/**
 * @author 李磊
 * @since 1.0
 */
public class Test {

    private static final String BASE_PATH = "D:/test/";

    static {
        // 判断文件父目录是否存在 不存在就创建
        if (!FileUtil.exist(BASE_PATH)) {
            FileUtil.mkdir(BASE_PATH);
        }
    }

    public static void main(String[] args) throws IOException {
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        // 设置分词返回数量 频率最高的600个词
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        // 最小分词长度
        frequencyAnalyzer.setMinWordLength(2);
        // 引入中文解析器
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        // 目标数据
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(ResourceUtil.getStream("kumo/rank"));
        final Dimension dimension = new Dimension(653, 390);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        // 设置边界
        wordCloud.setPadding(2);
        // 设置字体
        wordCloud.setKumoFont(new KumoFont(new Font(null, BOLD, 20)));
        // 背景图
        wordCloud.setBackground(new PixelBoundaryBackground(ResourceUtil.getStream("kumo/bg.png")));
        // 设置词云字体颜色 词频越高颜色越靠前
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile(BASE_PATH + "word-cloud.png");
    }
}