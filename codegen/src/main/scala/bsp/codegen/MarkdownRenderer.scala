package bsp.codegen

import bsp.codegen.Hint.Documentation
import bsp.codegen.JsonRPCMethodType.{Notification, Request}
import cats.syntax.all._
import software.amazon.smithy.model.shapes.ShapeId

import scala.collection.mutable.{Set => MSet}

object MarkdownRenderer {
  def render(tree: DocTree): String = {
    val visited = MSet.empty[ShapeId]
    val renderer = new MarkdownRenderer(tree, visited)
    renderer.render.get.mkString(System.lineSeparator())
  }
}

class MarkdownRenderer private (tree: DocTree, visited: MSet[ShapeId]) {

  import dsl._

  def render: Lines = {
    lines(
      "## Common shapes",
      newline,
      tree.commonShapes.foldMap(renderNode),
      newline,
      tree.services.foldMap(renderNode)
    )
  }

  def renderNode(id: ShapeId): Lines = tree.docNodes(id) match {
    case OperationDocNode(operation, inputNode, outputNode) =>
      lines(
        s"### ${operation.name}: ${methodTpe(operation)}",
        newline,
        documentation(operation.hints),
        newline,
        s"- method: `${operation.jsonRPCMethod}`",
        inputNode.foldMap(n => s"- params: `${n.getName()}`"),
        outputNode.foldMap(n => s"- result: `${n.getName()}`"),
        inputNode.foldMap(renderNode),
        outputNode.foldMap(renderNode),
        newline
      )
    case ServiceDocNode(shapeId, operations) => {
      if (shapeId.getName().toLowerCase().contains("server")) {
        lines(
          s"## BSP Server remote interface",
          newline,
          operations.foldMap(renderNode)
        )
      } else {
        lines(
          s"## BSP Client remote interface",
          newline,
          operations.foldMap(renderNode)
        )
      }
    }
    case ShapeDocNode(definition, members) => {
      if (visited.contains(definition.shapeId)) empty
      else
        {
          visited.add(definition.shapeId)
          lines(
            s"#### ${definition.shapeId.getName()}",
            newline,
            documentation(definition.hints),
            newline,
            tsBlock(definition),
            newline
          )
        } ++ lines(
          members.foldMap(renderNode)
        )
    }
  }

  def renderRest(doc: DocNode): Lines = doc match {
    case ShapeDocNode(definition, members) =>
      members.foldMap(renderNode)
    case _ => empty
  }

  def methodTpe(operation: Operation): String = operation.jsonRPCMethodType match {
    case Notification => "notification"
    case Request      => "request"
  }

  def documentation(hints: List[Hint]): Lines = Lines {
    hints.collect { case Documentation(string) => string.split(System.lineSeparator()).toList }
  }

  val tsRenderer = new TypescriptRenderer(None)

  def tsBlock(node: DocNode): Lines =
    node match {
      case ShapeDocNode(definition, _) => tsBlock(definition)
      case _                           => empty
    }

  def tsBlock(definition: Def): Lines =
    tsRenderer
      .render(definition)
      .map { tsCode =>
        lines(
          "```ts",
          tsCode,
          "```"
        )
      }
      .getOrElse(empty)

}
