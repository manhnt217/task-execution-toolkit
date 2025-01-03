import {Handle, NodeProps, Position} from "@xyflow/react";

const StartNode = (props: NodeProps) => {
  return (
    <>
      <Handle type="source" position={Position.Right} />
      <div className="bg-black-alpha-60 px-3 py-2 border-round-lg">START</div>
    </>
  );
}

export default StartNode;