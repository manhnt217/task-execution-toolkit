import React, {useCallback} from 'react';
import {ReactFlow, Controls, Background, BackgroundVariant, useNodesState, useEdgesState, addEdge} from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import StartNode from "./node/StartNode.tsx";
import EndNode from "./node/EndNode.tsx";

const DiagramComponent: React.FC = () => {

  const nodeTypes = { 'StartNode': StartNode, 'EndNode': EndNode };

  const initialNodes = [
    {id: '1', position: {x: 200, y: 200}, type: 'StartNode'},
    {id: '2', position: {x: 800, y: 200}, type: 'EndNode'},
  ];
  const initialEdges: { id: string; source: string; target: string }[] = [];

  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);

  const onConnect = useCallback(
    (connection) => setEdges((eds) => addEdge(connection, eds)),
    [setEdges],
  );

  return (
    <ReactFlow colorMode="dark"
               nodes={nodes}
               edges={edges}
               onNodesChange={onNodesChange}
               onEdgesChange={onEdgesChange}
               onConnect={onConnect}
               defaultEdgeOptions={{animated: true, type: 'bezier'}}
               nodeTypes={nodeTypes}
               fitView
    >
      <Controls/>
      <Background color={'#2671aa'} bgColor={'#ccc'} variant={BackgroundVariant.Dots} gap={25} size={1}/>
    </ReactFlow>
  );
};

export default DiagramComponent;