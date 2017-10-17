package utils;



import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
 
/**
 * @author       huqic_000
 *
 * @description   
MD5是一个安全的散列算法，输入两个不同的明文不会得到相同的输出值，根据输出值，
不能得到原始的明文，即其过程不可逆；所以要解密MD5没有现成的算法，只能用穷举法，
把可能出现的明文，用MD5算法散列之后，把得到的散列值和原始的数据形成一个一对一的映射表，
通过比在表中比破解密码的MD5算法散列值，通过匹配从映射表中找出破解密码所对应的原始明文。

对信息系统或者网站系统来说，MD5算法主要用在用户注册口令的加密，对于普通强度的口令加密，
可以通过以下三种方式进行破解：

（1）在线查询密码。一些在线的MD5值查询网站提供MD5密码值的查询，
输入MD5密码值后，如果在数据库中存在，那么可以很快获取其密码值。

（2）使用MD5破解工具。网络上有许多针对MD5破解的专用软件，
通过设置字典来进行破解。

（3）通过社会工程学来获取或者重新设置用户的口令。

因此简单的MD5加密是没有办法达到绝对的安全的，因为普通的MD5加密有多种暴力破解方式，
因此如果想要保证信息系统或者网站的安全，需要对MD5进行改造，增强其安全性
 */
public class MD5Utils {
	
	
	
    /**
     * 使用md5的算法进行加密
     * @param plainText     明文
     * @param salt          盐（就加邮箱吧）
     * @return  加密后文本
     */
    public static String md5(String plainText,String salt) {
        byte[] secretBytes = null;
        plainText += salt;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
     
    public static void main(String[] args) {
         
        System.out.println(md5("admin123","1367727256@qq.com"));
    }
 
}
