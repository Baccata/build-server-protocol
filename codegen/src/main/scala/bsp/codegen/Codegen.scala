package bsp.codegen

import software.amazon.smithy.model.Model

object Codegen {

  def run(outputDir: os.Path): List[os.Path] = {
    val model = ModelLoader.loadModel()
    val definitions = new SmithyToIR(model).definitions("bsp")
    val renderer = new Renderer("ch.epfl.scala.bsp4j")
    val codegenFiles = definitions.map(renderer.render)
    codegenFiles.map { cf =>
      val fullPath = outputDir / cf.path
      os.write.over(fullPath, cf.contents, createFolders = true)
      fullPath
    }
  }

}
