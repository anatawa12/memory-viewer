package com.anatawa12.mods.memoryViewer;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.util.Map;

public class MemoryViewerCoreMod implements IFMLLoadingPlugin {
    public MemoryViewerCoreMod() {
        initWindow();
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return "com.anatawa12.mods.memoryViewer.MemoryViewerContainer";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    private void initWindow() {
        JProgressBar alloc;
        JProgressBar used;
        JLabel timeLabel;
        JFrame mainFrame = new JFrame("memory-viewer");

        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(480, 200);
        mainFrame.setLocation(0, 0);
        mainFrame.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        timeLabel = new JLabel("time here");
        mainPanel.add(textPanel(timeLabel));

        mainPanel.add(barPanel(new JLabel("alloc"), alloc = new JProgressBar()));

        mainPanel.add(barPanel(new JLabel("used"), used = new JProgressBar()));

        mainFrame.getContentPane().add(mainPanel);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Thread thread = new Thread(new Runnable()
        {
            private final Color memoryGoodColor = new Color(0x78CB34);
            private final Color memoryWarnColor = new Color(0xE6E84A);
            private final Color memoryLowColor =  new Color(0xE42F2F);

            float memoryColorPercent = 0F;
            long memoryColorChangeTime = 0;

            @Override
            public void run()
            {
                while(true)
                {
                    int maxMemory = bytesToMb(Runtime.getRuntime().maxMemory());
                    int totalMemory = bytesToMb(Runtime.getRuntime().totalMemory());
                    int freeMemory = bytesToMb(Runtime.getRuntime().freeMemory());
                    int usedMemory = totalMemory - freeMemory;
                    float usedMemoryPercent = usedMemory / (float) maxMemory;

                    long time = System.currentTimeMillis();
                    if (usedMemoryPercent > memoryColorPercent || (time - memoryColorChangeTime > 1000))
                    {
                        memoryColorChangeTime = time;
                        memoryColorPercent = usedMemoryPercent;
                    }

                    Color memoryBarColor;
                    if (memoryColorPercent < 0.75f)
                    {
                        memoryBarColor = memoryGoodColor;
                    }
                    else if (memoryColorPercent < 0.85f)
                    {
                        memoryBarColor = memoryWarnColor;
                    }
                    else
                    {
                        memoryBarColor = memoryLowColor;
                    }

                    timeLabel.setText(stopWatch.toString());

                    alloc.setString(getMemoryString(totalMemory) + " / " + getMemoryString(maxMemory));
                    alloc.setMaximum(maxMemory);
                    alloc.setValue(totalMemory);

                    used.setString(getMemoryString(usedMemory) + " / " + getMemoryString(maxMemory));
                    used.setMaximum(maxMemory);
                    used.setValue(usedMemory);
                    used.setForeground(memoryBarColor);

                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException ignore) {
                    }
                }
            }

            private String getMemoryString(int memory)
            {
                return StringUtils.leftPad(Integer.toString(memory), 4, ' ') + " MB";
            }
        });
        mainFrame.setVisible(true);

        thread.start();
    }

    public JPanel barPanel(JLabel label, JProgressBar bar) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        int height = 80;
        panel.setMaximumSize(new Dimension(480, height));
        panel.add(label);
        panel.add(bar);

        int h2 = height / 2;
        label.setFont(label.getFont().deriveFont(12.0F));
        label.setPreferredSize(new Dimension(400, h2));
        label.setAlignmentX(0.5F);

        bar.setFont(bar.getFont().deriveFont(12.0F));
        bar.setForeground(Color.CYAN);
        bar.setStringPainted(true);
        bar.setUI(new BasicProgressBarUI() {
            protected Color getSelectionForeground() {
                return Color.BLACK;
            }

            protected Color getSelectionBackground() {
                return Color.BLACK;
            }
        });
        bar.setPreferredSize(new Dimension(400, h2));
        bar.setAlignmentX(0.5F);
        return panel;
    }

    public JPanel textPanel(JLabel label) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        int height = 40;
        panel.setMaximumSize(new Dimension(480, height));
        panel.add(label);

        label.setFont(label.getFont().deriveFont(12.0F));
        label.setPreferredSize(new Dimension(400, height));
        label.setAlignmentX(0.5F);
        label.setHorizontalAlignment(JLabel.CENTER);

        return panel;
    }

    private static int bytesToMb(long bytes)
    {
        return (int) (bytes / 1024L / 1024L);
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
