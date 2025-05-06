package com.huning.yurpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.huning.yurpc.registry.Registry;
import com.huning.yurpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类: 接口名 => (key => 实现类)
     * SPI服务可以不止加载Serializer, 在之后的扩展中还可以添加其他的服务
     * 在这里的loaderMap存储的就是一个接口对应的Map实现
     * 在对应的Map中存储并非是一个对象, 只是相关实现类的class, 可以通过getInstance方法创建对象, 减少内存开销
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象示例缓存(避免重复new), 类路径=> 对象实例, 单例
     * serializer => 反射形成的实例对象
     * serializer并不需要同一个类型有很多个, 只需要一个就可以使用了, 所以使用单例可以减少内存开销
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统spi目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载类列表
     * 因为做的并不是为了单独加载Serializer类的Loader, 目前在列表中只保存了Serializer.class
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     * 并不是加载所有类型的Serializer, 而是加载所有类型的SPI
     * 加载Serializer都是一次性加载进来的, 并不是选择用哪个就加载哪个
     * 而是将所有的都一次性转化成实例, 然后选择调用使用.
     * 在使用中可以通过在static中直接使用load进行加载, 也可以在所有项目完成后, 直接调用这个方法进行初始化
     */
    public static void loadAll(){
        log.info("加载所有的SPI");
        for(Class<?> aClass : LOAD_CLASS_LIST){
            load(aClass);
        }
    }

    /**
     * 获取某个接口的class
     * 每次是直接将一个接口中存储的所有的class对应key的那个进行实例化
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        //对应的接口不存在, 或者说并没有被加载进SPILoader中
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        //没有这个类
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在key=%s 的类型",tClassName,key));
        }

        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        //如果在存储instance的cache中不存在这个实例, 才进行实例化
        //但是, 在cache中只存储类名, 万一重复了怎么办(ps.getName()获得的是权限名, 不止有类的名称)
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            }catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }


    /**
     * 加载用户定义下的实现类
     * @param loadClass
     * @return 返回的hashmap对应实现类的name和实现实例
     */
    public static Map<String,Class<?>> load(Class<?> loadClass){
        log.info("加载类型为{} 的SPI", loadClass.getName());

        //扫描路径, 用户自定义的SPI优先级高于系统
        //先加载system, 后加载custom, 使用custom覆盖system创建的键值对.
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            //获取文件目录下中的文件内容
            //注意在文件目录下的相应文件名要与loadClass.getName()相同, 不然不会加载
            //之前一个bug就是调取Registry接口的全部实现失败, 因为全限名写错了.
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            for (URL resource : resources) {
                try{
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine())!= null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                }catch (Exception e){
                    log.error("spi load error",e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

}
