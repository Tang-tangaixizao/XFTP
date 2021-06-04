package Util;

import com.jcraft.jsch.SftpProgressMonitor;
import sample.Controller;
import sample.Main;
import java.text.NumberFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SftpMonitor implements SftpProgressMonitor, Runnable {
    // 文件的总大小
    private long maxCount;
    public long startTime = 0L;
    private long uploaded = 0;
    private boolean isScheduled = false;
    ScheduledExecutorService executorService;
    /**获取Controller
     * */
    Controller controller= (Controller) Main.controllers.get(Controller.class.getSimpleName());

    public SftpMonitor(long maxCount) {
        this.maxCount = maxCount;
    }

    public void run() {
        /**设置进度条区域可见
         * */
        controller.UPfileName.setVisible(true);
        controller.progressBar.setVisible(true);
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        String value = format.format((uploaded / (double) maxCount));
        Controller controller=(Controller) Main.controllers.get(Controller.class.getSimpleName());
        value=value.substring(0,value.indexOf("%"));
        controller.progressBar.setProgress(Double.valueOf(value)/100);
        if (uploaded == maxCount) {
            stop();
            long endTime = System.currentTimeMillis();
            /**如果传输时间小于100毫秒就延迟隐藏
             * */
            if((endTime - startTime)<100){
                /**隐藏进度条区域
                 * */
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                controller.UPfileName.setVisible(false);
                controller.progressBar.setVisible(false);
            }else{
                controller.UPfileName.setVisible(false);
                controller.progressBar.setVisible(false);
            }
        }
    }

    /**
     * 输出每个时间段的上传大小
     */
    public boolean count(long count) {
        if (!isScheduled) {
            createTread();
        }
        uploaded += count;
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 文件上传结束时调用
     */
    public void end() {
    }

    /**
     * 文件上传时开始调用
     */
    public void init(int op, String src, String dest, long max) {
        startTime = System.currentTimeMillis();
    }

    /**
     * 创建一个线程每隔一定时间，输出一下上传进度
     */
    public void createTread() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        // 0秒钟后开始执行，每1毫杪钟执行一次
        executorService.scheduleWithFixedDelay(this,
                0, 1, TimeUnit.MILLISECONDS);
        isScheduled = true;
    }

    /**
     * 停止方法
     */
    public void stop() {
        boolean isShutdown = executorService.isShutdown();
        if (!isShutdown) {
            executorService.shutdown();
        }
    }

}

