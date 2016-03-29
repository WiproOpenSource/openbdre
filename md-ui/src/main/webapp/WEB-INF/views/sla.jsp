<!DOCTYPE html>
<meta charset="utf-8">
<style>

body {
  font: 10px sans-serif;
}

.axis path,
.axis line {
  fill: none;
  stroke: #000;
  shape-rendering: crispEdges;
}

.bar {
  fill: steelblue;
}

.x.axis path {
  display: none;
}

</style>
<body>
<script>
var jsSLAMonitoringObjectList=[];
</script>
<%@ page import="java.util.*,com.wipro.ats.bdre.md.beans.SLAMonitoringBean,org.codehaus.jackson.map.ObjectMapper,org.codehaus.jackson.map.type.TypeFactory" %>
<%
 String slaMonitoringBeanList=request.getParameter("slaMonitoringBeanList");
  ObjectMapper mapper = new ObjectMapper();
  List<SLAMonitoringBean> list2 = mapper.readValue(slaMonitoringBeanList,
  TypeFactory.collectionType(List.class, SLAMonitoringBean.class));
          for(int i=0;i<list2.size();i++)
          {
          SLAMonitoringBean slaMonitoringBean=list2.get(i);
          int processId=slaMonitoringBean.getProcessId();
          long currentExecutionTime=slaMonitoringBean.getCurrentExecutionTime();
          long averageExecutionTime=slaMonitoringBean.getAverageExecutionTime();
          long sLATime=slaMonitoringBean.getsLATime();
           %>
              <script>
              function jsSLAMonitoringObject(processId, currentExecutionTime, averageExecutionTime, sLATime) {
                     this.processId = processId;
                     this.currentExecutionTime = currentExecutionTime;
                     this.averageExecutionTime = averageExecutionTime;
                     this.sLATime = sLATime;
                 }
               var slaBean=new jsSLAMonitoringObject("<%=processId %>","<%=currentExecutionTime %>","<%=averageExecutionTime %>","<%=sLATime %>");
                    jsSLAMonitoringObjectList.push(slaBean);
                    </script>
               <%
          }

%>
<div>
<script src="//d3js.org/d3.v3.min.js"></script>
<script>
var margin = {top: 20, right: 40, bottom: 30, left: 200},
    width = 1500 - margin.left - margin.right,
    height = 900 - margin.top - margin.bottom;

var x0 = d3.scale.ordinal()
    .rangeRoundBands([0, width], .1);

var x1 = d3.scale.ordinal();

var y = d3.scale.linear()
    .range([height, 0]);

var color = d3.scale.ordinal()
    .range(["#98abc5", "#8a89a6", "#d0743c"]);

var xAxis = d3.svg.axis()
    .scale(x0)
    .orient("bottom");

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")
    .tickFormat(d3.format(".2s"));

var svg = d3.select("body").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var data=[
{processId:'CA',Years:2704, to13Years:4499, to17Years:2159},
{processId:'TX',Years:2027, to13Years:3277, to17Years:1420},
{processId:'NY',Years:1208, to13Years:2141, to17Years:1058},
{processId:'FL',Years:1140, to13Years:1938, to17Years:925},
{processId:'IL',Years:894, to13Years:1558,  to17Years:725},
{processId:'PA',Years:737, to13Years:1345,  to17Years:679}
];

 function draw(data) {
  var ageNames = d3.keys(data[0]).filter(function(key) { return key !== "processId"; });

  data.forEach(function(d) {
    d.ages = ageNames.map(function(name) { return {name: name, value: +d[name]}; });
  });

  x0.domain(data.map(function(d) { return d.processId; }));
  x1.domain(ageNames).rangeRoundBands([0, x0.rangeBand()]);
  y.domain([0, d3.max(data, function(d) { return d3.max(d.ages, function(d) { return d.value; }); })]);

  svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);

  svg.append("g")
      .attr("class", "y axis")
      .call(yAxis)
    .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("Time in milliSeconds");

  var processId = svg.selectAll(".processId")
      .data(data)
    .enter().append("g")
      .attr("class", "processId")
      .attr("transform", function(d) { return "translate(" + x0(d.processId) + ",0)"; });

  processId.selectAll("rect")
      .data(function(d) { return d.ages; })
    .enter().append("rect")
      .attr("width", x1.rangeBand())
      .attr("x", function(d) { return x1(d.name); })
      .attr("y", function(d) { return y(d.value); })
      .attr("height", function(d) { return height - y(d.value); })
      .style("fill", function(d) { return color(d.name); });

  var legend = svg.selectAll(".legend")
      .data(ageNames.slice().reverse())
    .enter().append("g")
      .attr("class", "legend")
      .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

  legend.append("rect")
      .attr("x", width - 18)
      .attr("width", 18)
      .attr("height", 18)
      .style("fill", color);

  legend.append("text")
      .attr("x", width - 24)
      .attr("y", 9)
      .attr("dy", ".35em")
      .style("text-anchor", "end")
      .text(function(d) { return d; });

};
draw(jsSLAMonitoringObjectList);
</script>
</div>
<hr width="80%" COLOR="#6699FF" SIZE="2">
<center><b>ProcessIDs</b></center>