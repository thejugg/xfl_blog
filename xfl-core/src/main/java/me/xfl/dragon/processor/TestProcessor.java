package me.xfl.dragon.processor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.xfl.dragon.processor.renderer.TestFrontRenderer;
import me.xfl.jugg.annotation.RequestProcessing;
import me.xfl.jugg.annotation.RequestProcessor;
import me.xfl.jugg.servlet.HTTPRequestContext;
import me.xfl.jugg.servlet.HTTPRequestMethod;
import me.xfl.jugg.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;

/**
 * 测试用的页面处理类。
 * @author Administrator
 *
 */
@RequestProcessor
public final class TestProcessor{
	
	@RequestProcessing(value = "/helloworld", method = HTTPRequestMethod.GET)
	public void helloWorld(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response){
		final AbstractFreeMarkerRenderer renderer=new TestFrontRenderer();
		context.setRenderer(renderer);
		renderer.setTemplateName("hello.ftl");
		
		final Map<String, Object> dataModel = renderer.getDataModel();
		
		dataModel.put("hello", "Hello World! 你好世界！");
	}
}