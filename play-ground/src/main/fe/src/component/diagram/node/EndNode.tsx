import {Handle, NodeProps, Position} from "@xyflow/react";

const StartNode = (props: NodeProps) => {
  return (
    <>
      <Handle type="target" position={Position.Left} />
      <div className="bg-black-alpha-60 px-3 py-2 border-round-lg">END</div>
    </>
  );
}

export default StartNode;