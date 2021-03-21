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
package me.aijie.capital.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.aijie.capital.domain.Capital;
import me.aijie.capital.util.IdCardService;
import me.aijie.utils.ValidationUtil;
import me.aijie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.aijie.capital.repository.CapitalRepository;
import me.aijie.capital.service.CapitalService;
import me.aijie.capital.service.dto.CapitalDto;
import me.aijie.capital.service.dto.CapitalQueryCriteria;
import me.aijie.capital.service.mapstruct.CapitalMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.aijie.utils.PageUtil;
import me.aijie.utils.QueryHelp;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author dingaijie
* @date 2020-12-05
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class CapitalServiceImpl implements CapitalService {

    private final CapitalRepository capitalRepository;
    private final CapitalMapper capitalMapper;

    @Override
    public Map<String,Object> queryAll(CapitalQueryCriteria criteria, Pageable pageable){
        Page<Capital> page = capitalRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(capitalMapper::toDto));
    }

    @Override
    public List<CapitalDto> queryAll(CapitalQueryCriteria criteria){
        return capitalMapper.toDto(capitalRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CapitalDto findById(Integer id) {
        Capital capital = capitalRepository.findById(id).orElseGet(Capital::new);
        ValidationUtil.isNull(capital.getId(),"Capital","id",id);
        return capitalMapper.toDto(capital);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CapitalDto create(Capital resources) {
        return capitalMapper.toDto(capitalRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Capital resources) {
        Capital capital = capitalRepository.findById(resources.getId()).orElseGet(Capital::new);
        ValidationUtil.isNull( capital.getId(),"Capital","id",resources.getId());
        capital.copy(resources);
        capitalRepository.save(capital);
    }

    @Override
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            capitalRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CapitalDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CapitalDto capital : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" idCard",  capital.getIdCard());
            map.put(" contract",  capital.getContract());
            map.put(" name",  capital.getName());
            map.put(" date",  capital.getDate());
            map.put(" money",  capital.getMoney());
            map.put(" term",  capital.getTerm());
            map.put(" company",  capital.getCompany());
            map.put(" car",  capital.getCar());
            map.put(" oper",  capital.getOper());
            map.put(" operDate",  capital.getOperDate());
            map.put(" notes",  capital.getNotes());
            map.put(" address",  capital.getAddress());
            map.put(" code",  capital.getCode());
            map.put(" dueDate",  capital.getDueDate());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public boolean addCapital(Map<String, Object> params) throws Exception {
       if(params.get("file")==null){
           return false;
       }
       String file = (String) params.get("file");
        JSONArray jsonArray = JSONArray.fromObject(file);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        if(jsonArray.size()>0){
            for(int i=0;i<jsonArray.size();i++){
                JSONObject capitalJson = jsonArray.getJSONObject(i);
                log.info(capitalJson.toString());
                Capital capital = (Capital) JSONObject.toBean(capitalJson,Capital.class);

                if(capital.getDate().length() == 5){
                    Date date = dateFormat.parse("1900/01/01");
                    int day = Integer.parseInt(capital.getDate());
                    Calendar rightNow = Calendar.getInstance();
                    rightNow.setTime(date);
                    rightNow.add(Calendar.DATE, (day-2));
                    Date dt1 = rightNow.getTime();
                    capital.setDate(sdf.format(dt1));
                }

                Date dateStart = sdf.parse(capital.getDate());
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(dateStart);
                rightNow.add(Calendar.MONTH, Integer.parseInt(capital.getTerm().substring(0,2)));
                Date dt1 = rightNow.getTime();
                String dateEnd = sdf.format(dt1);
                String notes = null;
                if(StringUtils.isEmpty(capital.getCompany())){
                    notes = "登记车辆为承租人（身份证号："+capital.getIdCard()+"）在"+capital.getDate()+"到"+dateEnd+"的期间内租赁凯京融资租赁（上海）有限公司实际所有的租赁车辆，约定登记挂靠在个人名下";
                }else{
                    notes = "登记车辆为承租人（身份证号："+capital.getIdCard()+"）在"+capital.getDate()+"到"+dateEnd+"的期间内租赁凯京融资租赁（上海）有限公司实际所有的租赁车辆，约定登记挂靠在"+capital.getCompany()+"名下";
                }
                capital.setNotes(notes);
                String nativePlace= IdCardService.getIdCardAtt(capital.getIdCard());
                capital.setAddress(nativePlace);
                capital.setOperDate(new Timestamp(System.currentTimeMillis()));
                capital.setOper("aijie");
                Calendar rightNow2 = Calendar.getInstance();
                rightNow2.setTime(new Date());
                rightNow2.add(Calendar.MONTH, Integer.parseInt(capital.getTerm().substring(0,2)));
                Date dt2 = rightNow2.getTime();
                String dueDate = sdf.format(dt2);
                capital.setDueDate(dueDate);
                StringBuilder code = new StringBuilder();
                code.append("$(\"#regtimelimit\").val(").append(capital.getTerm().substring(0,2)).append(");");
                code.append("$(\"#countRealexpiredateDiv\").text('").append(dueDate).append("');");
                code.append("$(\"#personfillarchiveno\").val(").append(capital.getContract()).append(");$(\"#addMyselfBtn\").click();");
                code.append("$(\"#maincontractno\").val(").append(capital.getContract()).append(");");
                code.append("$(\"#maincontractcurrency\").val('CNY');");
                code.append("$(\"#maincontractsum\").val(").append(capital.getMoney()).append(");");
                code.append("$(\"#collateraldescribe\").val('").append(notes).append("');");
                code.append("$(\"#identificationcode\").val('").append(capital.getCar()).append("');$(\"#addccbmBtn\").click();");
                code.append("$(\"#leaseMode\").val('02');$(\"#leasedtype\").val('05');$(\"#leasedtype\").change();$(\"#leasedtype2\").val('0501');$(\"#addzlwBtn\").click();");
                code.append("$('#addOutPeople').modal('show');resetForm('addOutPeopleForm');$('#outModalTitle').html('添加');$('#debtortype').val('04');");
                code.append("showCRRInput('addOutPeople');$('#certificatetype').val('01');showCRRInputByCardType('addOutPeople');");
                code.append("$(\"#certificatecode\").val('").append(capital.getIdCard()).append("');$('#nationality').val('CHN');");
                code.append("changeCRRCountry('addOutPeople');$(\"#address\").val('").append(nativePlace).append("');");
                capital.setCode(code.toString());
                capitalMapper.toDto(capitalRepository.save(capital));
            }
        }

        return true;
    }

}