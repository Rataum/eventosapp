package com.eventosapp.eventosapp.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventosapp.eventosapp.models.Convidado;
import com.eventosapp.eventosapp.models.Evento;
import com.eventosapp.eventosapp.repository.ConvidadoRepository;
import com.eventosapp.eventosapp.repository.EventoRepository;

@Controller
public class EventoController {
	
	@Autowired
	private EventoRepository eventoRepository;
	
	@Autowired
	private ConvidadoRepository convidadoRepository;
	
	@RequestMapping(value="/cadastrarEvento", method=RequestMethod.GET)
	public String form() {
		return "evento/formEvento";
	}
	
	@RequestMapping(value="/cadastrarEvento", method=RequestMethod.POST)
	public String form(@Valid Evento evento, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");	
		} else {
			eventoRepository.save(evento);
			attributes.addFlashAttribute("mensagem", "Evento cadastrado com sucesso!");
		}
		
		return "redirect:/cadastrarEvento";
	}
	
	@RequestMapping("/eventos")
	public ModelAndView listaEventos() {
		ModelAndView modelAndView = new ModelAndView("index");
		Iterable<Evento> eventos = eventoRepository.findAll();
		modelAndView.addObject("eventos", eventos);
		return modelAndView;
	}
	
	@RequestMapping(value="/{codigo}", method=RequestMethod.GET)
	public ModelAndView detalheEvento(@PathVariable("codigo") long codigo) {
		Evento evento = eventoRepository.findByCodigo(codigo);
		ModelAndView modelAndView = new ModelAndView("evento/detalhesEvento");
		Iterable<Convidado> convidados = convidadoRepository.findByEvento(evento);
		
		modelAndView.addObject("evento", evento);
		modelAndView.addObject("convidados", convidados);
		
		return modelAndView;
	}
	
	@RequestMapping("/deletarEvento")
	public String deletarEvento(long codigo) {
		Evento evento = eventoRepository.findByCodigo(codigo);
		eventoRepository.delete(evento);
		return "redirect:/eventos";
	}
	
	@RequestMapping(value="/{codigo}", method=RequestMethod.POST)
	public String detalheEventoPost(@PathVariable("codigo") long codigo, @Valid Convidado convidado, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");	
		} else {
			Evento evento = eventoRepository.findByCodigo(codigo);
			convidado.setEvento(evento);
			convidadoRepository.save(convidado);
			attributes.addFlashAttribute("mensagem", "Convidado adicionado com sucesso!");
		}
		return "redirect:/{codigo}";
	}
	
	@RequestMapping("/deletarConvidado")
	public String deletarConvidado(String rg) {
		Convidado convidado = convidadoRepository.findByRg(rg);
		Evento evento = convidado.getEvento();
		long codigoLong = evento.getCodigo();
		String codigo = "redirect:/" + codigoLong;
		
		convidadoRepository.delete(convidado);
		
		return codigo;
	}

}
