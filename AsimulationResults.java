package Main;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import DataCollection.SpectrumUtilizationRate;
import Graph.GraphImplement;
import Network.CoreSC;
import Network.DijkstraShortestPathSearch;
import Network.FrequencySlots;
import Network.Link;
import Network.Link2;
import Network.LinkCores;
import Network.LinkID;
import Network.LoadTopology;
import Network.Node;
import Network.Node2;
import Network.NodeId;
import Network.TopologyParameters;
import Network.TopologySetup;
import Network.TopologyquantifiedDetails;
import RSAAgorithm.BaseAlgorithm;
import RSAAgorithm.FirstFit;
import RSAAgorithm.KFirstFit;
import RSAAgorithm.TACIAA;
import RSAAgorithm.TACIAAK;
import RSAAgorithm.XTAwareAlgorithm;

import TrafficGeneration.ArrivalServiceGenerator;
import TrafficGeneration.MM1ServiceGenerator;
import TrafficGeneration.Service;
import TrafficGeneration.Timestamp;
import Utility.WeightType;

import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.builder.UndirectedWeightedGraphBuilderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DataCollection.DataCollectionPerSC;
import DataCollection.ServiceBlockingProbability;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * 只进行耗时测试
 */
public class AsimulationResults {

    private static final Logger log = LoggerFactory.getLogger(AsimulationResults.class);

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static int days = 1;
    private static int dotsPerDay = 24;
  // private  static List<Double> alphas = Arrays.asList( 0.1, 0.2,0.3,0.4,0.5, 0.6, 0.7, 0.8, 0.9, 1.0,1.1, 1.2 );
  private  static List<Double> alphas = Arrays.asList(  0.1,0.3,0.4,0.5,0.7 );
 // private  static List<Double> alphas = Arrays.asList(  0.1);

    private static  List<Integer> predict_periods = Arrays.asList(  30 );  // time list
   // private static  List<Integer> predict_periods = Arrays.asList(  30,60,90,120,150,180,210,240,270,300 );  // time list


  // private  static List<Double> mapes = Arrays.asList( 0.0, 0.1, 0.2,0.3,0.4,0.5, 0.6, 0.7, 0.8, 0.9);
    private  static List<Double> mapes = Arrays.asList( 0.1);

// private  static List<Double> mapes = Arrays.asList( 1.0, 2.0,3.0,4.0,5.0, 6.0, 7.0, 8.0, 9.0);
//private static List<Integer> rhos = Arrays.asList(  30,40,50,60,70,80,90,100,110,120);  // rho list
private static List<Integer> rhos = Arrays.asList(  90,120);  // rho list

//private static List<Integer> rhos = Arrays.asList(  30);  // rho list

//private static List<Integer> rhos = Arrays.asList(  90);  // rho list

private static Calendar startTime = Calendar.getInstance();
private static ArrayList<Service> service1= new ArrayList();

private static int printtime =60*60*1000;

private static  int minToMs = 60*1000;

    /**
     * 第一个参数表示rho
     * 第二个参数表示预测准确度浮动
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
         

       
         /** Related statistics initialization **/
    	
    	
       // for (int pp : predict_periods) {

     //   	TACIAAK.predictionIntervalInMins = pp;
   //   	TACIAA.predictionIntervalInMins = pp;
    //    Calendar startTime = Calendar.getInstance();
    	   for (int n = 0; n < alphas.size(); n++) {
              double determinant = alphas.get(n);
     //   Calendar startTime = Calendar.getInstance();

    //  	Timer timer = new Timer();
		//TimerTask tt = new TimerTask(){
	
		//	public  void run(){
		   

       
        for (int rho : rhos) {


       // int rho = 90;

        	ArrivalServiceGenerator.rho_0 = rho;
        	ArrivalServiceGenerator.rho_1 = rho-10;
        	ArrivalServiceGenerator.rho_2 = rho-20;
        	ArrivalServiceGenerator.rho_3 = rho-30;



        TopologyParameters params = TopologySetup.getInstance().TopologyParams;
        System.out.println(Calendar.getInstance().getTime().toLocaleString());


        /********************************** initialize parameters ******************************/
        Calendar startTime = Calendar.getInstance();
        Calendar startTime2 = Calendar.getInstance();
        startTime2.clear();
        startTime2.set(2019, 8, 11, 6, 00, 00);
        startTime.clear();
      startTime.set(2019, 8, 11, 6, 00, 00);
      //  startTime.set(2019, 8, 11, 6, 30, 21);
    //  System.out.println(startTime2.getTime().toLocaleString());

  //      if ( Calendar.getInstance() == startTime) {

        for (int i = 0; i < params.networks.size(); i++) {
        	TopologyquantifiedDetails net = params.networks.get(i);
            log.info("/******************************* {} ***************************/", net.name);
            // 在OnionServiceGenerator中，businessArea，residentialArea，rouBias都是无用的
           ArrivalServiceGenerator generator = new ArrivalServiceGenerator(params.timeInterval,
    	//	MM1ServiceGenerator generator = new  MM1ServiceGenerator(params.timeInterval,

                    params.rouBias,
                    params.rouBusinessPeak,
                    params.rouResidentialPeak,
                    params.miu,
                    params.minRequiredSlotNum,
                    params.maxRequiredSlotNum,
                    net.businessArea,
                    net.residentialArea,
                    net.vertexNum,
                    startTime,
                    days);
   

            ArrayList<Service> services = generator.generateServices();
    
      //  ArrayList<Service> service1=generator.PriorityServicesOrder(services, startTime, startTime2);
        //   ArrayList<Service> services = generator.generateGlobalServices();
          //  ArrayList<Timestamp> serviceTimestamp = generator.generateServicesOrder(service1);
           ArrayList<Timestamp> serviceTimestamp = generator.generateServicesOrder(services);

			
            log.info("The start time of service is {}, the end time of service is {}.",
                    format.format(services.get(0).getStartTime().getTime()),
                    format.format(services.get(services.size() - 1).getStartTime().getTime()));

            /************************** use RSA algorithm *****************************************/
     //   int ksp[] = {1,2,3};
//        int ksp[] = {1,2};

     int ksp[] = {2};
   for (int m=0; m<ksp.length; m++) {
            int k_value = ksp[m];
       //      int k_value = 2;


            ArrayList<SimpleWeightedGraph<Node2, Link2>> network = parseNets();
           Set<Integer> wholeVertexes = generateVertexes(net.vertexNum);
           // Set<Integer> BusinessVertexes = generateVertexes(net.businessArea.size());

            
       
        /*   FirstFit Algrith1;
            if (k_value==1) {
            	Algrith1 = new FirstFit( services, serviceTimestamp, network.get(i));
             } else {
            	 Algrith1 = new KFirstFit(k_value, services, serviceTimestamp,network.get(i));
               //  System.out.println(ffRSA.getPassedServices().size());

             }
             checkSlotsEmpty(network.get(i));
             double sim_start = Calendar.getInstance().getTimeInMillis();
             Algrith1.Execute();;
             double sim_end = Calendar.getInstance().getTimeInMillis();
             log.info("************************************************************");
             outputData(Algrith1, startTime, days, wholeVertexes );
             double total_bp = ServiceBlockingProbability.getInstance().calculateBP(
            		 Algrith1.getPassedServices().size(), Algrith1.getBlockedServices().size());
            // System.out.println(rho +"K:" +k_value+"cost_time:"+(sim_end - sim_start)+"bp:"+total_bp);
             System.out.println(rho +"K:" +k_value+"cost_time:"+(sim_end - sim_start)+"bp:"+total_bp+"--Time:-"+ startTime2.getTime().toLocaleString());

             LinkUtilizationOutputData(Algrith1.getLinkUtilizationData());
             LinkCrosstalkCalculations(Algrith1.getLinkCrosstalkaffectedSlots());*/

                  System.out.println("*************************************************************2");

            
         //         for (int n = 0; n < alphas.size(); n++) {
           //           double determinant = alphas.get(n);
     
            
           for (int pp : predict_periods) {

            	TACIAAK.predictionIntervalInMins = pp;
            	TACIAA.predictionIntervalInMins = pp;


           for (int mp = 0; mp < mapes.size(); mp++) {
           //	CrosstalkAlgo.mape = mapes.get(mp);
  //              TACIAAK.mape = 0.0;
                 TACIAA.mape=0.0;
                 TACIAAK.mape = mapes.get(mp);
                 TACIAA.mape=mapes.get(mp);
        

               //  int k=2;
         FirstFit Algrith;
             if (k_value == 1) {
                	 Algrith = new TACIAA( determinant,services,serviceTimestamp,network.get(i));
                } else {
                	 Algrith = new TACIAAK( k_value, determinant,services,serviceTimestamp,network.get(i));
                 }
         
             
  
                 checkSlotsEmpty(network.get(i));
               double sim_start = Calendar.getInstance().getTimeInMillis();
             //    sim_start = Calendar.getInstance().getTimeInMillis();
             
                System.out.println("*************************************************************1");

                Algrith.Execute();
            //    fit.Execute();

                 double sim_end = Calendar.getInstance().getTimeInMillis();
               //  sim_end = Calendar.getInstance().getTimeInMillis();

                 
               outputData(Algrith, startTime, days, wholeVertexes );
        //         outputData(Algrith, startTime, days, BusinessVertexes );

              //  outputData(fit, startTime, days, wholeVertexes );


                 double    total_bp = ServiceBlockingProbability.getInstance().calculateBP(
               //       total_bp = ServiceBlockingProbability.getInstance().calculateBP(

                		 Algrith.getPassedServices().size(), Algrith.getBlockedServices().size());
              
               //   System.out.println(rho +"K:" +k_value+"alphas:"+determinant +"mape:" +TACIAAK.mape +"predict_time:"+ pp+"cost_time:"+(sim_end - sim_start)+"bp:"+total_bp);
                  System.out.println(rho +"K:" +k_value+"alphas:"+determinant +"mape:" +TACIAAK.mape +"predict_time:"+ pp+"cost_time:"+(sim_end - sim_start)+"bp:"+total_bp +"--Time:-"+ startTime2.getTime().toLocaleString());
                  LinkUtilizationOutputData(Algrith.getLinkUtilizationData());
                  LinkCrosstalkCalculations(Algrith.getLinkCrosstalkaffectedSlots());

                 
              // LinkUtilizationOutputData(network.get(i));

                      System.out.println("*************************************************************2");


          
   }
       //   }
             //    }
            //   }
          }
            }
      }
        System.out.println(Calendar.getInstance().getTime().toLocaleString());
     //   System.out.println(startTime.getTime().toLocaleString());


          }
}
			}
 

    	 private static void LinkUtilizationOutputData( ArrayList<Double> spectrumUtilIzation) {

    		  double num = spectrumUtilIzation.size();
    	  //  	System.out.println(spectrumUtilIzation.size());

    	        double totalUtilization = 0;
    	        double totalUtilization1 = 0;

    	        for (double Linkutil : spectrumUtilIzation) {
    	           totalUtilization = totalUtilization+Linkutil;   
    	   //         totalUtilization1 = (totalUtilization+Linkutil)/ num ;   

    	        }
    	    
	            totalUtilization1 = totalUtilization/ num ;    
    	     
    	System.out.println("******************************LINKSET UTILIZATION*******************************");
    	

    	System.out.println(totalUtilization1);
    	    }
    	 private static void LinkCrosstalkCalculations( ArrayList<Double> AverageCrosstalkSlots) {
    		 double BR = 0.05;
    	        double PCB = 4*Math.pow(10, 6);
    	        double CCk = 4*Math.pow(10, -4);
    	        double CPA = 4*Math.pow(10, -5);
    	        
    	        double L = 1000*Math.pow(10, 3);
    	     //   ραΦŋβ
    	        double R = 0.05;
    	        double CCŋ = 2*Math.pow(10,-5);
    	        double BRΦ= 50*Math.pow(10, -3);
    	        double PCρ= 4*Math.pow(10, 6);
    	        
    	        double CPw= 45*Math.pow(10, -6);
    	        double XT;
    	        double XTlog10;
    	        double AverageXT;
    	        int n =6;
    	        double h;
    	        double s;

    	      
    	            
    	              //  XT = ((n-n*Math.exp(-(n+1)*2*k*k*R*i*L/B/A))/(1+n*Math.exp(-(n+1)*2*k*k*R*i*L/B/A)));
    	              //  XT = ((n-n*Math.exp(-(n+1)*2*k*k*R*i*L/B/A))/(1+n*Math.exp(-(n+1)*2*k*k*R*i*L/B/A)));
                   
    	                  h=2*CCk*BR/PCB*CPA; 
                     //     XT=s*(1-Math.exp(-(n+1)*h*L));

   		  double num = AverageCrosstalkSlots.size();
   	  //  	System.out.println(spectrumUtilIzation.size());

   	        double totalCrosstalkOcupiedslots = 0;
   	        double totalXTSlots1 = 0;

   	        for (double LinkCrostalkOccupiedslots : AverageCrosstalkSlots) {
   	        	totalCrosstalkOcupiedslots = totalCrosstalkOcupiedslots+LinkCrostalkOccupiedslots;   
   	   //         totalUtilization1 = (totalUtilization+Linkutil)/ num ;   

   	        }
   	    
	            totalXTSlots1 = totalCrosstalkOcupiedslots/ num ;    
               // XT=totalXTSlots1*(1-Math.exp(-(n+1)*h*L))/(1+n*Math.exp(-(n+1)*h*L));
                XT=totalXTSlots1*(1-Math.exp(-(n+1)*h*L))/(1+totalXTSlots1*Math.exp(-(n+1)*h*L));

                XTlog10 = 10*Math.log10(XT);


   	     
   	System.out.println("******************************CROSSTALK CALCULATIONS*******************************");
    	System.out.println( totalXTSlots1);


   	System.out.println( XTlog10);
   //	System.out.println( XT);

   	    }
   	
    	
    	
/**
 *     private static void LinkUtilizationOutputData(SimpleWeightedGraph<Node2, Link2> graph) {


        SpectrumUtilizationRate ourInstance =  SpectrumUtilizationRate.getInstance();
     /*   System.out.println("******************************LINK UTILIZATION*******************************111111111112");
          for (Link2 edge: path ) {
             double LinkUtilization=ourInstance.calculateUR(edge);
                       System.out.println(LinkUtilization);
          }*
System.out.println("******************************LINKSET UTILIZATION*******************************");
//double LinkSetUtilization=ourInstance.calculateUR(path.get(0));
double LinkSetUtilization=ourInstance.calculateUR( graph);

System.out.println(LinkSetUtilization);
    }*/
    
    /**
     *
     * @param RSA
     * @param startTime
     * @param days
     * @param vertexes
     * @param area
     */
    private static void outputData(
           // BasicRSAAlg RSA, Calendar startTime, int days, Set<Integer> vertexes, String area, String algName) {
    	BaseAlgorithm RSA, Calendar startTime, int days, Set<Integer> path) {
        ServiceBlockingProbability serviceBP = ServiceBlockingProbability.getInstance();

        int year = startTime.get(Calendar.YEAR);
        int month = startTime.get(Calendar.MONTH);
        int day = startTime.get(Calendar.DAY_OF_MONTH);
        Calendar zeroStartTime = Calendar.getInstance();
        zeroStartTime.set(year, month, day, 6, 0, 0);
        zeroStartTime.set(Calendar.MILLISECOND, 0);

        Calendar zeroEndTime = Calendar.getInstance();
        zeroEndTime.setTimeInMillis(zeroStartTime.getTimeInMillis());
//        zeroEndTime.add(Calendar.DAY_OF_YEAR, days);
        zeroEndTime.add(Calendar.HOUR_OF_DAY, 12);
        int dividedNum = days*dotsPerDay;
        ArrayList<Double> bps = serviceBP.calculateBP(zeroStartTime, zeroEndTime, RSA.getPassedServices(),
                RSA.getBlockedServices(), dividedNum, path);

        log.info("The start and end of bp is {} and {} in {}.",
                format.format(zeroStartTime.getTime()),
                format.format(zeroEndTime.getTime()));
             //   algName);
     //   log.info("The BPs of {} area in {} are : ", area, algName);
        // output
        for (double bp : bps) {
            System.out.print(bp+",");
        }
        System.out.println("");
    }



    /**
     * check if there exists slots that are not released after RSA and Defragmentation algorithm.
     * @param graph graph
     */
    private static void checkSlotsEmpty(SimpleWeightedGraph<Node2, Link2> graph) {
        for (Link2 edge : graph.edgeSet()) {
            //for (FrequencySlots slot : edge.getSlots()) {
        	List<LinkCores>sdmcorelist=edge.getCoreList();
                for (LinkCores sdmCores : sdmcorelist) {
      	          List<FrequencySlots> wavelengths =sdmCores.getWavelength();
     			 for (int i=0; i<wavelengths.size(); i++) {

               // if (slot.getIsOccupied()) {
                    if (wavelengths.get(i).getIsOccupied()) {
	
                    //	wavelengths.get(i).setOccupiedServiceIndex(0);
                  //  }
                 //   else {
                    throw new RuntimeException("there is a slot that isn't released after RSA and " +
                            "defragmentation algorithm at least.");
               }
            }
        }
    }
    }


    private static Set<Integer> generateVertexes(int num) {
        Set<Integer> rtn = Sets.newHashSet();
        for (int i=1; i<=num; i++) {
            rtn.add(i);
        }
      //  System.out.println(rtn);
        return rtn;
    }


    /**
     *
     * @return
     */
    public static ArrayList<SimpleWeightedGraph<Node2, Link2>> parseNets() {
        List<TopologyquantifiedDetails> netsParams = TopologySetup.getInstance().TopologyParams.networks;

       // List<SingleEonNet> netsParams = Bootstrap.getInstance().netParams.networks;
        ArrayList<SimpleWeightedGraph<Node2, Link2>> list =
                Lists.newArrayListWithCapacity(netsParams.size());
        for (int i=0; i<netsParams.size(); i++) {
            UndirectedWeightedGraphBuilderBase builderBase = SimpleWeightedGraph.builder(Link2.class);
            TopologyquantifiedDetails eonNet = netsParams.get(i);
            ArrayList<Node2> vertexList = Lists.newArrayListWithCapacity(eonNet.vertexNum);
            // add vertexes
            for (int vertexIndex=1; vertexIndex<=eonNet.vertexNum; vertexIndex++) {
            	Node2 vertex = new Node2(vertexIndex, true);
                builderBase.addVertex(vertex);
                vertexList.add(vertex);
            }
            // add edges
            for (Pair<Integer,Integer> edge : eonNet.edges) {
                builderBase.addEdge(vertexList.get(edge.getFirst()-1),
                        vertexList.get(edge.getSecond()-1),
                        1.0);
               // System.out.println(edge);

            }

            list.add((SimpleWeightedGraph<Node2, Link2>) builderBase.build());
        }
   //     System.out.println(list);

        return list;
    }


private static Map<NodeId, Map<NodeId, List<Link>>> randomSrcToRandomDst(GraphImplement graph) {
    Map<NodeId, Map<NodeId, List<Link>>> rtn = new HashMap<NodeId, Map<NodeId, List<Link>>>(graph.getNodesNum());
    for (Node src : graph.getNodes().values()) {
        DijkstraShortestPathSearch dSearch = new DijkstraShortestPathSearch(graph, false, WeightType.OPTICAL_WEIGHT, src);
       dSearch.calculateShortestDistances(); //dSearch for the shortest path after sorting
       rtn.put(src.getNodeId(), dSearch.getAllDsts());
     //     System.out.println(dSearch.getAllDsts());
        //  System.out.println(graph.getNodes().values());
    }

    return rtn;
}
}
