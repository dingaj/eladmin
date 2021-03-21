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
package me.aijie.capitalhistory.rest;

import me.aijie.annotation.Log;
import me.aijie.capitalhistory.domain.CapitalHistory;
import me.aijie.capitalhistory.service.CapitalHistoryService;
import me.aijie.capitalhistory.service.dto.CapitalHistoryQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author dingaijie
* @date 2020-12-05
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "历史中登数据管理")
@RequestMapping("/api/capitalHistory")
public class CapitalHistoryController {

    private final CapitalHistoryService capitalHistoryService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('capitalHistory:list')")
    public void download(HttpServletResponse response, CapitalHistoryQueryCriteria criteria) throws IOException {
        capitalHistoryService.download(capitalHistoryService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询历史中登数据")
    @ApiOperation("查询历史中登数据")
    @PreAuthorize("@el.check('capitalHistory:list')")
    public ResponseEntity<Object> query(CapitalHistoryQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(capitalHistoryService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增历史中登数据")
    @ApiOperation("新增历史中登数据")
    @PreAuthorize("@el.check('capitalHistory:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CapitalHistory resources){
        return new ResponseEntity<>(capitalHistoryService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改历史中登数据")
    @ApiOperation("修改历史中登数据")
    @PreAuthorize("@el.check('capitalHistory:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CapitalHistory resources){
        capitalHistoryService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除历史中登数据")
    @ApiOperation("删除历史中登数据")
    @PreAuthorize("@el.check('capitalHistory:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Integer[] ids) {
        capitalHistoryService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}