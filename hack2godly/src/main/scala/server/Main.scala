package server

import server.WebApp.Arguments
import unfiltered.response.ResponseString
import unfiltered.request._

/**
 * Created by jannis on 12/5/15.
 */
object Main extends WebApp[Args] {

  class ServerApp(args: Arguments) extends unfiltered.filter.Plan {
    def intent = {
      case req@GET(Path(Seg("test" :: Nil)) & Params(params)) =>
        ResponseString("Das hier ist ein simpler test")

      case req@GET(Path(Seg("test2" :: Nil)) & Params(params)) =>
        val paramsMap = params.toMap[String, Seq[String]]
        ResponseString(paramsMap.getOrElse("wurst", Seq("nix geworden")).mkString(","))
    }
  }

  def htmlRoot: String = "learn4good"

  def setup(args: Args): unfiltered.filter.Plan = {
    new ServerApp(args)
  }

}

class Args extends Arguments {

}
