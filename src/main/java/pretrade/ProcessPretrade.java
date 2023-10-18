package pretrade;

import org.apache.commons.collections4.multiset.AbstractMapMultiSet;
import simplemodel.SimpleLearning;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Process raw pretrade data from itch output
 *
 * Input
 * timestamp (millis after midnight),Ticker,Order,Type,Shares,Price,MPID
 *
 * Output
 * timestamp, Ticker, Order, Type, Shares, Price, algoID, next_timestamp, next_transaction_type, next_quantity
 *
 */
public class ProcessPretrade {


    /**
     * gathers orders into an order -> pretrade
     * for every successive timestamp, adds one microsecond
     * @param name
     */
    public LinkedHashMap<Long, Pretrade> readPretrade(String name) {

        ClassLoader classLoader = SimpleLearning.class.getClassLoader();
        File file = new File(classLoader.getResource("data/pretrade/" + name).getFile());


        LinkedHashMap<Long, Pretrade> pretrade_map = new LinkedHashMap<Long, Pretrade>();

        long prev_millisecond = 0;
        int n_trades = 0;
        int m_count = 0;
        try (BufferedReader fis = new BufferedReader(new FileReader(file))) {

            String content = fis.readLine();

            while ((content = fis.readLine()) != null) {

                if(content.isEmpty()) {
                    break;
                }

                String[] p = content.split("[,]+");

                //timestamp
                long timestamp = Long.parseLong(p[0]);

                if(timestamp != prev_millisecond)
                    m_count = 0;
                else
                    m_count++;

                long order_number = Long.parseLong(p[2]);

                //if trade (market, iceberg, dark in lit)
                if(order_number == 0) {
                    n_trades++;
                    order_number = n_trades;
                }

                String trans_type = p[3];

                int volume = Integer.parseInt(p[4]);
                float price = Float.parseFloat(p[5])/1000f;


                //if order doesn't exist yet, create
                Pretrade pretrade = pretrade_map.get(order_number);
                if(pretrade == null) {

                    pretrade = new Pretrade();
                    pretrade.setTimestamp(timestamp);
                    pretrade.setMicrosecond(m_count);
                    pretrade.setOrder_number(order_number);
                    pretrade.setTransaction_type(trans_type);
                    pretrade.setPrice(price);
                    pretrade.setVolume(volume);
                    pretrade_map.put(order_number, pretrade);
                }
                else {
                    Amend a = new Amend(timestamp, m_count, trans_type, volume);
                    pretrade.addNextValue(a);
                }

                prev_millisecond = timestamp;

            }

            fis.close();
        }

        catch (IOException e) {
            throw new RuntimeException(e);
        }


        return pretrade_map;

    }



    public static void main(String[] args) {

        ProcessPretrade pre = new ProcessPretrade();

        LinkedHashMap<Long, Pretrade> pretrade_map = pre.readPretrade("20220804_NVDA.csv");

        System.out.println(pretrade_map.size());

    }

}
