package com.example.aoplib;
import android.annotation.TargetApi;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by ZKM on 2017/5/16.
 */

@Aspect
public class Testaop {

//    private static final String METHOD_EXECUTION = "execution(* *..MainActivity+.onCreate(..))";
//
//
//    @Pointcut(METHOD_EXECUTION)
//    public void methodExecution() {
//    }
//
//
//    @Around("methodExecution()")
//    public void aroundMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
//        joinPoint.proceed();
//        String result = "-----------------------------MainExecution------------------------------------------";
//        System.out.println(result);
//    }
//    private static final String METHOD_EXECUTION
//        =  "(execution(* *..Activity+.*(..)) ||execution(* *..Layout+.*(..))) && target(Object) && this(Object)";
//
//
//    @Pointcut(METHOD_EXECUTION)
//    public void methodExecution() {
//    }
//
//
//    @Around("methodExecution()")
//    public void aroundMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
//        joinPoint.proceed();
//        String result = "-----------------------------MainExecution------------------------------------------";
//        System.out.println(result);
//        //初始化计时器
//        long start =System.currentTimeMillis();
//        //开始监听
//        //调用原方法的执行。
//        Object result1 = joinPoint.proceed();
//        //监听结束
//        long last =System.currentTimeMillis()-start;
//        //获取方法信息对象
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        String className;
//        //获取当前对象，通过反射获取类别详细信息
//        className = joinPoint.getThis().getClass().getName();
//
//        String methodName = methodSignature.getName();
//        System.out.println(className+" runtime:   "+methodName+last);
    private static final String POINTCUT_METHOD =
        "(execution(* *..Activity+.*(..)) ||execution(* *..Layout+.*(..))) && target(Object) && this(Object)";
    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithDebugTrace() {}

    @Around("methodAnnotatedWithDebugTrace()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        final StopWatch stopWatch = new StopWatch();
       // String[] startCpu=getCpuInfo();
        double startCpu=cpu("com.example.five");
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
       // String[] endCpu=getCpuInfo();
        double endCpu=cpu("com.example.five");
        Log.d(className, buildLogMessage(methodName, stopWatch.getTotalTimeMillis())+ startCpu+endCpu);
        return result;
    }

    /**
     * Create a log message.
     *
     * @param methodName A string with the method name.
     * @param methodDuration Duration of the method in milliseconds.
     * @return A string representing message.
     */
    private static String buildLogMessage(String methodName, long methodDuration) {
        StringBuilder message = new StringBuilder();
        message.append("Gintonic --> ");
        message.append(methodName);
        message.append(" --> ");
        message.append("[");
        message.append(methodDuration);
        message.append("ms");
        message.append("]");

        return message.toString();
    }
//        String msg =  buildLogMessage(methodName, stopWatch.getTotalTime(1));
//        DebugLog.outPut(new Path());    //日志存储
//        DebugLog.ReadIn(new Path());    //日志读取
public String[] getCpuInfo() {
    String str1 = "/proc/cpuinfo";
    String str2="";
    String[] cpuInfo={"",""};
    String[] arrayOfString;
    try {
        FileReader fr = new FileReader(str1);
        BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
        str2 = localBufferedReader.readLine();
        arrayOfString = str2.split("\\s+");
        for (int i = 2; i < arrayOfString.length; i++) {
            cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
        }
        str2 = localBufferedReader.readLine();
        arrayOfString = str2.split("\\s+");
        cpuInfo[1] += arrayOfString[2];
        localBufferedReader.close();
    } catch (IOException e) {
    }
    return cpuInfo;
}
    public static double cpu(String PackageName) throws IOException {
        double Cpu = 0;
        try{
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("adb shell top -n 1| grep "+PackageName);
            try {
                if (proc.waitFor() != 0) {
                    System.err.println("exit value = " + proc.exitValue());
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        proc.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer();
                String line = null;
                while ((line = in.readLine()) != null) {
                    stringBuffer.append(line+" ");


                }
                String str1=stringBuffer.toString();
                String  str3=str1.substring(str1.indexOf(PackageName)-43,str1.indexOf(PackageName));
                String cpu= str3.substring(0,4);
                cpu=cpu.trim();
                Cpu=Double.parseDouble(cpu);

            } catch (InterruptedException e) {
                System.err.println(e);
            }finally{
                try {
                    proc.destroy();
                } catch (Exception e2) {
                }
            }
        }
        catch (Exception StringIndexOutOfBoundsException)
        {

            System.out.print("请检查设备是否连接");

        }

        return Cpu;

    }
}
