//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2014 uniCenta
//    http://www.unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.payment;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.util.RoundUtils;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author JG uniCenta
 */
public class JPaymentKeyboard extends javax.swing.JPanel implements JPaymentInterface {
    
    private JPaymentSelect m_notifier;

    private double m_dPaid;
    private double m_dTotal;
    
    private String transaction;
    
    /** Creates new form JPaymentCash
     * @param notifier */
    public JPaymentKeyboard(JPaymentSelect notifier) {
        
        m_notifier = notifier;
        initComponents();          
    }
    
    private DefaultTableModel model;
    private TableModel getModel(){
        if(model==null){
            model = new javax.swing.table.DefaultTableModel(new String [] {
                "Methode", "Betrag"
            },0);
        }
        return model;
    }
    
    
    /**
     *
     * @param customerext
     * @param dTotal
     * @param transID
     */
    @Override
    public void activate(CustomerInfoExt customerext, double dTotal, String transID) {
        m_dTotal = RoundUtils.round(dTotal);
        transaction=transID;
    }

    /**
     *
     * @return
     */
    @Override
    public PaymentInfo executePayment() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        model=null;
        jTable1.setModel(getModel());
        return this;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jTendered = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jAmount = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jPanel3);

        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(jPanel2, java.awt.BorderLayout.EAST);

        jPanel4.setLayout(new java.awt.BorderLayout());

        m_jTendered.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jTenderedKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                m_jTenderedKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                m_jTenderedKeyReleased(evt);
            }
        });
        jPanel4.add(m_jTendered, java.awt.BorderLayout.CENTER);

        jTable1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jTable1.setModel(getModel());
        jTable1.setIntercellSpacing(new java.awt.Dimension(4, 4));
        jTable1.setRowHeight(40);
        jScrollPane1.setViewportView(jTable1);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jAmount.setBackground(new java.awt.Color(255, 255, 255));
        jAmount.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jAmount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jAmount.setMinimumSize(new java.awt.Dimension(60, 15));
        jAmount.setOpaque(true);
        jAmount.setPreferredSize(new java.awt.Dimension(60, 45));
        jPanel4.add(jAmount, java.awt.BorderLayout.PAGE_END);

        add(jPanel4, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void stateTransistion(String action)
    {
        Double value = m_dTotal;
        if(jAmount.getText().length()>0)
            value=RoundUtils.round(Double.parseDouble(jAmount.getText())/100d);
        
        System.out.println(String.valueOf("value: "+value));
        System.out.println(String.valueOf("m_dTotal: "+m_dTotal));
        
        PaymentInfo payment=null;
        
        try{
            Integer.parseInt(action);
            jAmount.setText(jAmount.getText()+action);
            return;
        }catch(NumberFormatException e){}
        
        switch(action){        
            case "bar": //cash
                payment = new PaymentInfoCash_original(Math.min(value,m_dTotal), value);
                break;
            case "gutschein": //gutschein
                payment = nonCash(value,"paperin");
                break;
            case "kreditkarte": //kreditkarte
                payment = nonCash(value,"magcard");
                break;
            case "ec": //ec
                payment = nonCash(value, "cheque");
                break;
            case "cancel": //clear
                jAmount.setText("");
                return;
            default:
                return;
        }
        Boolean done= RoundUtils.compare(value,m_dTotal) >= 0;
        if (payment != null) {
            if (!done)
                addLine(payment, value);
            m_notifier.addPayment(payment, done);
        }
        jAmount.setText("");
    }
    
    private void m_jTenderedKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jTenderedKeyTyped
        
    }//GEN-LAST:event_m_jTenderedKeyTyped

    private void m_jTenderedKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jTenderedKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ESCAPE)
            m_notifier.dispose();
    }//GEN-LAST:event_m_jTenderedKeyPressed

    private void m_jTenderedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jTenderedKeyReleased
        // TODO add your handling code here:
        for(String action : ((AppConfig)m_notifier.app.getProperties()).getKeyAction(evt))
            stateTransistion(action);
    }//GEN-LAST:event_m_jTenderedKeyReleased
    
    private PaymentInfo nonCash(Double value,String name){
        if(RoundUtils.compare(value,m_dTotal)>0) return null;
        return new PaymentInfoTicket(value, name);
    }
    
    private void addLine(PaymentInfo payment,Double value){
        model.insertRow(0,new Object[]{payment.printIntName()
                ,Formats.CURRENCY.formatValue(value)});
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jAmount;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField m_jTendered;
    // End of variables declaration//GEN-END:variables
    
}
