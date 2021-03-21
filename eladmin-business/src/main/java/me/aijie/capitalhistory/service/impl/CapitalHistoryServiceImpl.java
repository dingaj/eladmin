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
package me.aijie.capitalhistory.service.impl;

import me.aijie.capitalhistory.domain.CapitalHistory;
import me.aijie.utils.ValidationUtil;
import me.aijie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.aijie.capitalhistory.repository.CapitalHistoryRepository;
import me.aijie.capitalhistory.service.CapitalHistoryService;
import me.aijie.capitalhistory.service.dto.CapitalHistoryDto;
import me.aijie.capitalhistory.service.dto.CapitalHistoryQueryCriteria;
import me.aijie.capitalhistory.service.mapstruct.CapitalHistoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.aijie.utils.PageUtil;
import me.aijie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author dingaijie
* @date 2020-12-05
**/
@Service
@RequiredArgsConstructor
public class CapitalHistoryServiceImpl implements CapitalHistoryService {

    private final CapitalHistoryRepository capitalHistoryRepository;
    private final CapitalHistoryMapper capitalHistoryMapper;

    @Override
    public Map<String,Object> queryAll(CapitalHistoryQueryCriteria criteria, Pageable pageable){
        Page<CapitalHistory> page = capitalHistoryRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(capitalHistoryMapper::toDto));
    }

    @Override
    public List<CapitalHistoryDto> queryAll(CapitalHistoryQueryCriteria criteria){
        return capitalHistoryMapper.toDto(capitalHistoryRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CapitalHistoryDto findById(Integer id) {
        CapitalHistory capitalHistory = capitalHistoryRepository.findById(id).orElseGet(CapitalHistory::new);
        ValidationUtil.isNull(capitalHistory.getId(),"CapitalHistory","id",id);
        return capitalHistoryMapper.toDto(capitalHistory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CapitalHistoryDto create(CapitalHistory resources) {
        return capitalHistoryMapper.toDto(capitalHistoryRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CapitalHistory resources) {
        CapitalHistory capitalHistory = capitalHistoryRepository.findById(resources.getId()).orElseGet(CapitalHistory::new);
        ValidationUtil.isNull( capitalHistory.getId(),"CapitalHistory","id",resources.getId());
        capitalHistory.copy(resources);
        capitalHistoryRepository.save(capitalHistory);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            capitalHistoryRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CapitalHistoryDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CapitalHistoryDto capitalHistory : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" idCard",  capitalHistory.getIdCard());
            map.put(" contract",  capitalHistory.getContract());
            map.put(" name",  capitalHistory.getName());
            map.put(" date",  capitalHistory.getDate());
            map.put(" money",  capitalHistory.getMoney());
            map.put(" term",  capitalHistory.getTerm());
            map.put(" company",  capitalHistory.getCompany());
            map.put(" car",  capitalHistory.getCar());
            map.put(" oper",  capitalHistory.getOper());
            map.put(" operDate",  capitalHistory.getOperDate());
            map.put(" notes",  capitalHistory.getNotes());
            map.put(" address",  capitalHistory.getAddress());
            map.put(" code",  capitalHistory.getCode());
            map.put(" dueDate",  capitalHistory.getDueDate());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}