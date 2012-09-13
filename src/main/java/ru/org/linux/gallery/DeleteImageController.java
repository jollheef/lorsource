/*
 * Copyright 1998-2012 Linux.org.ru
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.org.linux.gallery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.org.linux.auth.AccessViolationException;
import ru.org.linux.group.GroupPermissionService;
import ru.org.linux.site.Template;
import ru.org.linux.topic.PreparedTopic;
import ru.org.linux.topic.Topic;
import ru.org.linux.topic.TopicDao;
import ru.org.linux.topic.TopicPrepareService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/delete_image")
public class DeleteImageController {
  @Autowired
  private ImageDao imageDao;

  @Autowired
  private TopicDao topicDao;

  @Autowired
  private TopicPrepareService prepareService;

  @Autowired
  private GroupPermissionService permissionService;

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView deleteForm(
          @RequestParam(required = true) int id,
          HttpServletRequest request
  ) throws Exception {
    Template tmpl = Template.getTemplate(request);

    Image image = imageDao.getImage(id);

    Topic topic = topicDao.getById(image.getTopicId());

    PreparedTopic preparedTopic = prepareService.prepareTopic(topic, request.isSecure(), tmpl.getCurrentUser());

    if (!permissionService.isEditable(preparedTopic, tmpl.getCurrentUser())) {
      throw new AccessViolationException("Вы не можете редактировать эту тему");
    }

    ModelAndView mv = new ModelAndView("delete_image");

    mv.addObject("image", image);
    mv.addObject("preparedTopic", preparedTopic);

    return mv;
  }
}