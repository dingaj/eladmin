package me.aijie.capital.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class IdCardService {

    //需要购买
    public static String appkey = "";
    public static String sign = "";

    /**
     * 生成身份证信息
     * @param cardNo
     * @return 返回身份证信息
     * @throws UnsupportedEncodingException
     */
    public static String getIdCardDetail(String cardNo) throws UnsupportedEncodingException{
        // 获取身份证信息
        IdCard idcard = getIdCardInfo(cardNo);

        // 存储文本信息
        StringBuffer news = new StringBuffer();

        if (idcard != null) {
            news.append("所属地区:"+idcard.getAtt()).append("\n");
            news.append("出生日期:"+idcard.getBorn()).append("\n");
            news.append("性别:"+idcard.getSex()).append("\n");
        }

        if(news.length() == 0){
            news.append("身份证号码").append(cardNo).append("不存在,请重新输入!");
        }

        return news.toString();
    }

    public static String getIdCardAtt(String cardNo) throws UnsupportedEncodingException{
        // 获取身份证信息
        IdCard idcard = getIdCardInfo(cardNo);
        return idcard.getAtt();
    }
    /**
     * 获取身份证信息
     * @param cardNo
     * @return 返回身份证信息
     */
    public static IdCard getIdCardInfo(String cardNo){
        URL url = null;
        IdCard idCard = new IdCard();
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            url = new URL("http://api.k780.com/?app=idcard.get&idcard="+cardNo+"&appkey=55424&sign=2087898a51ccdcac1b224f8643325e94&format=xml");
            Document doc = builder.parse(url.openStream());
            NodeList node = doc.getElementsByTagName("result");
            for(int i=0;i<node.getLength();i++){
                String idcard = "";
                String born = "";
                String sex = "";
                String att = "";
                if(doc.getElementsByTagName("idcard").item(i).getFirstChild() != null){
                    idcard = doc.getElementsByTagName("idcard").item(i).getFirstChild().getNodeValue();
                }
                if(doc.getElementsByTagName("born").item(i).getFirstChild() != null){
                    born = doc.getElementsByTagName("born").item(i).getFirstChild().getNodeValue();
                }
                if(doc.getElementsByTagName("sex").item(i).getFirstChild() != null){
                    sex = doc.getElementsByTagName("sex").item(i).getFirstChild().getNodeValue();
                }
                if(doc.getElementsByTagName("att").item(i).getFirstChild() != null){
                    att = doc.getElementsByTagName("att").item(i).getFirstChild().getNodeValue();
                }
                idCard.setIdCard(idcard);
                idCard.setBorn(born);
                idCard.setSex(sex);
                idCard.setAtt(att);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return idCard;
    }

    public static void main(String[] args){
        try {
            System.out.print(getIdCardAtt("120223199410261232"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
