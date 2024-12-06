package com.sample;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This is a sample class to launch a rule.
 */
public class BookBest {

    public static void main(String[] args) {
        // KieServices is the factory for all KIE services
        KieServices ks = KieServices.Factory.get();

        // From the kie services, a container is created from the classpath
        KieContainer kc = ks.getKieClasspathContainer();

        new BookBest().init(kc, true);
    }

    public BookBest() {
    }

    public void init(KieContainer kc, boolean exitOnClose) {
        
        //The callback is responsible for populating working memory and
        // fireing all rules
        BookBestUI ui = new BookBestUI( new CheckoutCallback( kc ) );
        ui.createAndShowGUI(exitOnClose);
    }
    
    public static class BookBestUI extends JPanel {

        private static final long serialVersionUID = 510l;

        private JTextArea        output;

        private CheckoutCallback callback;

        /**
         * Build UI using specified items and using the given callback to pass the
         * items and jframe reference to the drools application
         * 
         * @param listData
         * @param callback
         */
        public BookBestUI(
                          CheckoutCallback callback) {
            super( new BorderLayout() );
            this.callback = callback;

            //Create main vertical split panel
            JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
            add( splitPane,
                 BorderLayout.CENTER );

            //create top half of split panel and add to parent
            JPanel topHalf = new JPanel();
            topHalf.setLayout( new BoxLayout( topHalf,
                                              BoxLayout.X_AXIS ) );
            topHalf.setBorder( BorderFactory.createEmptyBorder( 5,
                                                                5,
                                                                0,
                                                                5 ) );
            topHalf.setMinimumSize( new Dimension( 400,
                                                   50 ) );
            topHalf.setPreferredSize( new Dimension( 450,
                                                     250 ) );
            splitPane.add( topHalf );

            //create bottom top half of split panel and add to parent
            JPanel bottomHalf = new JPanel( new BorderLayout() );
            bottomHalf.setMinimumSize( new Dimension( 400,
                                                      50 ) );
            bottomHalf.setPreferredSize( new Dimension( 450,
                                                        300 ) );
            splitPane.add( bottomHalf );

            //Container that list container that shows available store items
            JPanel listContainer = new JPanel( new GridLayout( 1,
                                                               1 ) );
            listContainer.setBorder( BorderFactory.createTitledBorder( "List" ) );
            topHalf.add( listContainer );
            
            JPanel tableContainer = new JPanel( new GridLayout( 1,
                                                                1 ) );
            tableContainer.setBorder( BorderFactory.createTitledBorder( "Table" ) );
            topHalf.add( tableContainer );

            //Create output area, imbed in scroll area an add to bottomHalf parent
            //Scope is at instance level so it can be easily referenced from other
            // methods
            output = new JTextArea( 1,
                                    10 );
            output.setEditable( false );
            JScrollPane outputPane = new JScrollPane( output,
                                                      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            bottomHalf.add( outputPane,
                            BorderLayout.CENTER );

            this.callback.setOutput( this.output );
        }

        /**
         * Create and show the GUI
         */
        public void createAndShowGUI(boolean exitOnClose) {
            //Create and set up the window.
            JFrame frame = new JFrame( "Pet Store Demo" );
            frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);

            setOpaque( true );
            frame.setContentPane( this );

            //Display the window.
            frame.pack();
            frame.setLocationRelativeTo(null); // Center in screen
            frame.setVisible( true );
        }
    }
    
    public static class CheckoutCallback {
        KieContainer kcontainer;
        JTextArea     output;

        public CheckoutCallback(KieContainer kcontainer) {
            this.kcontainer = kcontainer;
        }

        public void setOutput(JTextArea output) {
            this.output = output;
        }

        /**
         * Populate the cart and assert into working memory Pass Jframe reference
         * for user interaction
         * 
         * @param frame
         * @param items
         * @return cart.toString();
         */
    }
    
    
}
