from pyqtgraph.Qt import QtCore, QtGui
import pyqtgraph as pg
import pyqtgraph.exporters
from dateutil.relativedelta import *
from dateutil.easter import *
from dateutil.rrule import *
from dateutil.parser import *
import datetime
import numpy as np
import parsedatetime
import calendar, datetime, time

app = pg.mkQApp()
view = pg.GraphicsLayoutWidget()

w1 = view.addPlot()
now = pg.ptime.time()

users = dict()
endpoints = dict()
cal = parsedatetime.Calendar()
filename = "1524217193371"
type = "one-year"

with open(filename + ".csv") as f:
    lis=[line.split(",") for line in f]        # create a list of lists
    for i,x in enumerate(lis):
        if "/init/addpatient" in x[3] or "/init/reset" in x[3]: continue

        ###
        # Setup for different users graphs
        ###
        if not x[0] in users:
            users[x[0]] = { "x" : [], "y" : [] }

        date = parse(x[1])

        date = str(date)[:-6]
        p = "%Y-%m-%d %H:%M:%S"
        epoch = datetime.datetime.utcfromtimestamp(0)
        date = int((datetime.datetime.strptime(date, p) - epoch).total_seconds())


        users[x[0]]["x"].append(date)
        users[x[0]]["y"].append(int(x[2]))

        ###
        # Setup for different endpoint graphs
        ###
        if not x[3] in endpoints:
            endpoints[x[3]] = {}
        if not x[0] in endpoints[x[3]]:
            endpoints[x[3]][x[0]] = { "x" : [], "y": [] }
        endpoints[x[3]][x[0]]["x"].append(date)
        endpoints[x[3]][x[0]]["y"].append(int(x[2]))
        print str(i) + " of " + str(len(lis))

y = 0
for i in users:
    break
    print users[i]["x"], users[i]["y"]
    pg.plot(users[i]["x"], users[i]["y"], pen=(y,6))
    y += 1

print endpoints.keys()
for x in endpoints:
    y = 0
    app = pg.mkQApp()
    view = pg.GraphicsLayoutWidget()
    testView = pg.PlotWidget()
    w1 = view.addPlot()
    now = pg.ptime.time()
    thisX = x[1:].replace("\n", "").replace("/", "-")
    testView.getPlotItem().setTitle(thisX)
    testView.getPlotItem().setLabel("left", "Response time", "ms")
    testView.getPlotItem().setLabel("bottom", "Epoch time", "ms")
    legend = testView.getPlotItem().addLegend(None, (-0, 30))
    testView.getPlotItem().enableAutoRange(testView.getPlotItem().getViewBox().XYAxes, True)
    #legend.setParentItem(testView.getPlotItem())
    for i in endpoints[x]:
        plt = w1.plot(endpoints[x][i]["x"], endpoints[x][i]["y"], pen=(y,len(endpoints[x].keys())))
        element = testView.plot(endpoints[x][i]["x"], endpoints[x][i]["y"], pen=(y,len(endpoints[x].keys())))
        legend.addItem(element, name=i)
        y += 1
    #testView.getPlotItem().addLegend()
    #testView.addItem(legend)
    #view.show()
    viewBox = testView.getPlotItem().getViewBox()

    exporter = pg.exporters.ImageExporter(testView.getPlotItem())
    #app.exec_()
    # set export parameters if needed
    #exporter.params['width'] = 1000
    #exporter.params.param('width').setValue(1600, blockSignal=exporter.widthChanged)
    #exporter.params.param('height').setValue(1080, blockSignal=exporter.heightChanged)
    exporter.parameters()['width'] = 1920   # (note this also affects height parameter)
    exporter.parameters()['height'] = 1080   # (note this also affects height parameter)
    exporter.params.param('width').setValue(1520, blockSignal=exporter.widthChanged)
    exporter.params.param('height').setValue(1080, blockSignal=exporter.heightChanged)
    # save to file
    #print x.replace("\n", ""), filename

    exporter.export(thisX + "-" + type + '.png')
