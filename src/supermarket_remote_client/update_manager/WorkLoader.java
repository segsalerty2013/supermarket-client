/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket_remote_client.update_manager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 * @author Segun
 */
public abstract class WorkLoader{

    private JDialog dialog;
    private JPanel wrapper;
    private JLabel label;
    private String txt;

    private SwingWorker<Void, Void> workT;
    private Component component;//component to lock while waiting and get
    //released when done
    private Timer workTimer;

    public WorkLoader(){

    }
    
    public void passJob(SwingWorker<Void, Void> w, Component c, String t){
        workT = w;
        component = c;
        txt = "<html><div style='text-align:center'><span style='color:red'>Please wait ... </span><br /><span>" + t + "</span></div></html>";
        init();
        dialog.setVisible(true);
        dialog.repaint();
        workTimer = new Timer(500, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(workT.getState() != SwingWorker.StateValue.DONE){
                    //while this worker is not done. then continue to roll
                    //component.setVisible(false);
                    component.setVisible(false);
                    //component.getParent().getParent().getParent().setEnabled(false);
                }
                else{
                   //this worker is done... then perform when the user
                    //put in whenDone method
                   workTimer.stop();
                   dialog.dispose();
                   //component.setVisible(true);
                   //component.setVisible(true);
                   //component.getParent().getParent().getParent().setEnabled(true);
                   whenDone();//invoke the method whendone()
                }
            }
        });
        workTimer.start();
    }

    private void init(){
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        dialog = new JDialog();
        label = new JLabel();
        label.setIcon(new ImageIcon(getClass().getResource("loading.gif")));
        label.setText(txt);
        label.setIconTextGap(5);
        label.setBackground(Color.WHITE);
        label.setPreferredSize(new Dimension(250, 75));
        wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setPreferredSize(new Dimension(250, 75));
        wrapper.setBackground(Color.WHITE);

        dialog.setSize(250, 75);
        //dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setUndecorated(true);
        dialog.setLocation((d.width - 250) / 2, (d.height - 75) / 2);
        dialog.setAlwaysOnTop(true);

        wrapper.add(label, BorderLayout.CENTER);
        dialog.getContentPane().add(wrapper);
    }
    public void pauseLoader(){
        workTimer.setRepeats(false);
        dialog.hide();
    }

    public void resumeLoader(){
        workTimer.setRepeats(true);
        workTimer.restart();
        dialog.show();
    }

    public abstract void whenDone(); //override this method -- what to do
    //when the worker is done

}
