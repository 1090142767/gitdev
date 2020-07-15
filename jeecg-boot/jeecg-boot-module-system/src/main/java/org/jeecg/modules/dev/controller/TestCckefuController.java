package org.jeecg.modules.dev.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.dev.entity.TestCckefu;
import org.jeecg.modules.dev.service.ITestCckefuService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: test_cckefu
 * @Author: jeecg-boot
 * @Date:   2020-07-09
 * @Version: V1.0
 */
@Api(tags="test_cckefu")
@RestController
@RequestMapping("/dev/testCckefu")
@Slf4j
public class TestCckefuController extends JeecgController<TestCckefu, ITestCckefuService> {
	@Autowired
	private ITestCckefuService testCckefuService;

	 @Autowired
	 private RedisUtil redisUtil;


	@RequestMapping("hi")
	public Result<?> hi(@RequestParam String sex){
		QueryWrapper<TestCckefu> qw = new QueryWrapper<TestCckefu>();
		qw.eq("sex",sex);
		List<TestCckefu> list = testCckefuService.list(qw);

		List<Object> cclist1 = redisUtil.lGet("cclist", 0, -1);
		if(cclist1 != null && cclist1.size() > 0){
			redisUtil.del("cclist");
		}
		//for (TestCckefu cc :list) {
			redisUtil.lSet("cclist",list);
		//}
		List<Object> cclist = redisUtil.lGet("cclist", 0, -1);
		List<TestCckefu> listobj = (List<TestCckefu>)cclist.get(0);
		for (TestCckefu obj:listobj  ) {
			System.out.println(obj);
		}
		return Result.ok(list);
	}

	/**
	 * 分页列表查询
	 *
	 * @param testCckefu
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "test_cckefu-分页列表查询")
	@ApiOperation(value="test_cckefu-分页列表查询", notes="test_cckefu-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TestCckefu testCckefu,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TestCckefu> queryWrapper = QueryGenerator.initQueryWrapper(testCckefu, req.getParameterMap());
		Page<TestCckefu> page = new Page<TestCckefu>(pageNo, pageSize);
		IPage<TestCckefu> pageList = testCckefuService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param testCckefu
	 * @return
	 */
	@AutoLog(value = "test_cckefu-添加")
	@ApiOperation(value="test_cckefu-添加", notes="test_cckefu-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TestCckefu testCckefu) {
		testCckefuService.save(testCckefu);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param testCckefu
	 * @return
	 */
	@AutoLog(value = "test_cckefu-编辑")
	@ApiOperation(value="test_cckefu-编辑", notes="test_cckefu-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TestCckefu testCckefu) {
		testCckefuService.updateById(testCckefu);
		return Result.ok("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "test_cckefu-通过id删除")
	@ApiOperation(value="test_cckefu-通过id删除", notes="test_cckefu-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		testCckefuService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "test_cckefu-批量删除")
	@ApiOperation(value="test_cckefu-批量删除", notes="test_cckefu-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.testCckefuService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "test_cckefu-通过id查询")
	@ApiOperation(value="test_cckefu-通过id查询", notes="test_cckefu-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TestCckefu testCckefu = testCckefuService.getById(id);
		if(testCckefu==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(testCckefu);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param testCckefu
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TestCckefu testCckefu) {
        return super.exportXls(request, testCckefu, TestCckefu.class, "test_cckefu");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TestCckefu.class);
    }

}
