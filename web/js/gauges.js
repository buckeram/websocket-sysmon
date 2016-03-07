var createGauge = function(divId) {
    var gauge = c3.generate({
        bindto: divId,
        data: {
            columns: [
                ['data', 0.0]
            ],
            type: 'gauge'
        },
        color: {
            pattern: ['#60B044', '#F6C600', '#F97600', '#FF0000'], // the color levels for the percentage values.
            threshold: {
                values: [30, 60, 90, 100]
            }
        },
        size: {
            height: 180
        }
    });
    return gauge;
};

var ramGauge = createGauge('#memory-used');
var processGauge = createGauge('#process-load');
var systemGauge = createGauge('#system-load');

var connection = new WebSocket("ws://" + window.location.host + window.location.pathname + "sysmon");
connection.onmessage = function(event) {
    var msg = JSON.parse(event.data);
    ramGauge.load({columns: [['data', msg.usedMem]] });
    processGauge.load({columns: [['data', msg.processLoad]] });
    systemGauge.load({columns: [['data', msg.systemLoad]] });
};
connection.onopen = function() {
    console.log("Connected to Websocket!");
};
connection.onerror = function(event) {
    console.log("An error occurred: " + event.data);
};
connection.onclose = function(event) {
    console.log("Connection closed: " + event.reason);
    // Reset gauges to zero when the WebSocket connection is closed
    var gauges = [ramGauge, processGauge, systemGauge];
    for (var i = 0; i < gauges.length; i++) {
        gauges[i].load({columns: [['data', 0.0]] })
    }
};

window.onbeforeunload = function() {
    connection.close();
};

