/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.aijie.capital.rest;

import lombok.extern.slf4j.Slf4j;
import me.aijie.annotation.Log;
import me.aijie.capital.domain.Capital;
import me.aijie.capital.service.CapitalService;
import me.aijie.capital.service.dto.CapitalQueryCriteria;
import me.aijie.utils.StringUtils;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
* @website https://el-admin.vip
* @author dingaijie
* @date 2020-12-05
**/
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "中登生成管理")
@RequestMapping("/api/capital")
public class CapitalController {

    private final CapitalService capitalService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('capital:list')")
    public void download(HttpServletResponse response, CapitalQueryCriteria criteria) throws IOException {
        capitalService.download(capitalService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询中登生成")
    @ApiOperation("查询中登生成")
    @PreAuthorize("@el.check('capital:list')")
    public ResponseEntity<Object> query(CapitalQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(capitalService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增中登生成")
    @ApiOperation("新增中登生成")
    @PreAuthorize("@el.check('capital:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Capital resources){
        return new ResponseEntity<>(capitalService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改中登生成")
    @ApiOperation("修改中登生成")
    @PreAuthorize("@el.check('capital:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Capital resources){
        capitalService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除中登生成")
    @ApiOperation("删除中登生成")
    @PreAuthorize("@el.check('capital:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Integer[] ids) {
        capitalService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Log("导入未录入数据")
    @ApiOperation("删除中登生成")
    @PreAuthorize("hasAnyRole('admin')")
    @PostMapping("/import")
    public ResponseEntity<Object> excelImport(@RequestParam Map<String, Object> params, HttpSession session){
        try {
            capitalService.addCapital(params);
        } catch (Exception e) {
            log.info("导入报错："+e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

}