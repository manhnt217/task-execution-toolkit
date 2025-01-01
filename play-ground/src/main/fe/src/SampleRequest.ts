import axios from "axios";

const sampleRequest = () => axios.post('/api/task', JSON.parse('{\n' +
  '    "input": {\n' +
  '        "name": "Lanne",\n' +
  '        "age": 21,\n' +
  '        "address": "Kyoto"\n' +
  '    },\n' +
  '    "taskDescription": {\n' +
  '        "name": "simpleFunction",\n' +
  '        "inputClass": "java.util.Map",\n' +
  '        "outputClass": "java.util.Map",\n' +
  '        "group": {\n' +
  '            "activities": [\n' +
  '                {\n' +
  '                    "type": "MAPPER",\n' +
  '                    "name": "p1",\n' +
  '                    "inputMapping": "{\\"category\\": \\"high\\", \\"important\\": true, \\"rate\\": -5.0}"\n' +
  '                },\n' +
  '                {\n' +
  '                    "type": "MAPPER",\n' +
  '                    "name": "p2",\n' +
  '                    "inputMapping": "{\\"category\\": \\"low\\"}"\n' +
  '                }\n' +
  '            ],\n' +
  '            "links": [\n' +
  '                {\n' +
  '                    "from": "START",\n' +
  '                    "to": "p1",\n' +
  '                    "guard": ".START.age > 10"\n' +
  '                },\n' +
  '                {\n' +
  '                    "from": "START",\n' +
  '                    "to": "p2",\n' +
  '                    "guard": "<otherwise>"\n' +
  '                },\n' +
  '                {\n' +
  '                    "from": "p1",\n' +
  '                    "to": "END"\n' +
  '                },\n' +
  '                {\n' +
  '                    "from": "p2",\n' +
  '                    "to": "END"\n' +
  '                }\n' +
  '            ]\n' +
  '        }\n' +
  '    }\n' +
  '}'));

export default sampleRequest;