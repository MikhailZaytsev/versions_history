package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.Campaign;
import ru.plantarum.core.service.CampaignService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    @PostMapping
    @ResponseBody
    public Page<Campaign> list(@RequestBody PagingRequest pagingRequest) {
        return campaignService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllCampaigns() {
        return "show-all-campaigns";
    }

    @GetMapping({"/add", "/edit"})
    public String addCampaignForm(@RequestParam(required = false) Long id, Model model) {
        Campaign campaign = Campaign.builder().build();
        if (id != null) {
            campaign = campaignService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#addCampaignForm: entity by id %s  not found", id)));
        }
        model.addAttribute("campaign", campaign);
        return "add-campaign";
    }

    @PostMapping("/edit")
    public String editCampaign(@RequestParam Long id, @Valid Campaign campaign, BindingResult bindingResult, Model model) {
        if (!campaignService.exists(id)) {
            throw new EntityNotFoundException(String.format("#editCampaignForm:  entity by id %s  not found", id));
        }
        campaign.setIdCampaign(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("campaign", campaign);
            return "add-campaign";
        } else {
            campaignService.save(campaign);
            return "redirect:/campaigns/all";
        }
    }

    @PostMapping("/add")
    public String addCampaign(@Valid @ModelAttribute("campaign") Campaign campaign, BindingResult bindingResult,
                              Model model) {
        if (campaignService.findByName(campaign.getCampaignName()) != null) {
            bindingResult.rejectValue("campaignName", "", "Уже существует");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("campaign", campaign);
            return "add-campaign";
        }
        campaignService.save(campaign);
        return "redirect:/campaigns/all";
    }

    @GetMapping("/delete")
    public String deleteCampaign(@RequestParam Long id) {
        if (campaignService.exists(id)) {
            campaignService.save(campaignService.deleteCampaign(id));
        }
        return "redirect:/campaigns/all";
    }

}
