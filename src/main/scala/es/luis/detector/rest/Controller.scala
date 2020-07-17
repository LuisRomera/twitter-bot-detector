package es.luis.detector.rest

import es.luis.detector.service.UserProcesor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RestController}

@Service
@RestController
class Controller @Autowired() (env: Environment){

  @RequestMapping(value = Array("/user/{name}"), method = Array(RequestMethod.GET))
  def greeting(@PathVariable("name") name: String): String = {
    "val result = new UserProcesor(env).analyzerUser(name)"
  }
}
