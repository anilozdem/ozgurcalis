//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.Scanner;
//import javax.swing.JOptionPane;
//
//
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
///**
// *
// * @author Anıl
// */
//public class Tasks {
//    
//
//    private ArrayList<TaskFields> tasks;           
//    Scanner inFile;
//    
//
//    public Tasks() {
//        
//        try {
//            tasks = new ArrayList<TaskFields>();
//            
//            this.inFile = new Scanner(new File("tasks.txt"));
//            inFile.useDelimiter("-");           
//            while(inFile.hasNext()) {
//                tasks.add(new TaskFields(inFile.nextInt(),inFile.next(),inFile.next(),inFile.next(),inFile.next()));
//                //JOptionPane.showMessageDialog(null, tasks.get(0).getId()+tasks.get(0).getName()+
//                        //tasks.get(0).getHints()+tasks.get(0).getProcessName()+tasks.get(0).getRelatedTasks());
//            }
//        
//            inFile.close();
//        } catch (FileNotFoundException e) {
//            JOptionPane.showMessageDialog(null, "File does not exist!");
//        }
//    }
//    
//    public TaskFields getProductByPosition(int position){
//		return tasks.get(position);
//	}
//	public int getCatalogSize(){
//		return tasks.size();
//	}
//
//}
