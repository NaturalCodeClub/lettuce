package gg.m2ke4u.skylight.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import oshi.SystemInfo;

public class StatsCommand extends Command {
    public StatsCommand(){
        super("sksstatus");
    }

    public final static SystemInfo SYSTEM_INFO = new SystemInfo();

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.isOp()){
            sender.sendMessage(ChatColor.RED+"No permission to execute this command!");
            return true;
        }
        StringBuffer results = new StringBuffer();
        // Paper start - Further improve tick handling
        double[] tps = org.bukkit.Bukkit.getTPS();
        String[] tpsAvg = new String[tps.length];

        for ( int i = 0; i < tps.length; i++) {
            tpsAvg[i] = format( tps[i] );
        }
        results.append(ChatColor.GREEN+"\n  Current Processors:"+SYSTEM_INFO.getHardware().getProcessors().length+"\n")
                .append(ChatColor.GREEN+"   System:"+SYSTEM_INFO.getOperatingSystem().getFamily()+"-"+SYSTEM_INFO.getOperatingSystem().getVersion()+"\n")
                .append(ChatColor.GREEN+"   Current Memory:"+((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory())+"/"+Runtime.getRuntime().maxMemory())+"\n")
                .append(ChatColor.GREEN+"   TPS:"+ org.apache.commons.lang.StringUtils.join(tpsAvg, ", "));
        sender.sendMessage(results.toString());
        return true;
    }

    private static String format(double tps) // Paper - Made static
    {
        return ( ( tps > 18.0 ) ? ChatColor.GREEN : ( tps > 16.0 ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > 20.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
    }
}
